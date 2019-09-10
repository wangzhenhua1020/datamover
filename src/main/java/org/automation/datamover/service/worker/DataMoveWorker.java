package org.automation.datamover.service.worker;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.automation.datamover.aspect.ConnectionHolder;
import org.automation.datamover.bean.Constant;
import org.automation.datamover.bean.db.DataMoverConfigVar;
import org.automation.datamover.bean.db.DataMoverInst;
import org.automation.datamover.bean.ext.ConfigRunningData;
import org.automation.datamover.bean.ext.DataMoverConfigDetail;
import org.automation.datamover.configuration.db.DynamicDataSourceContextHolder;
import org.automation.datamover.service.DataMoverConfigService;
import org.automation.datamover.service.DataMoverInstService;
import org.automation.datamover.service.DataMoverService;
import org.automation.datamover.util.ShellExecutor;
import org.automation.datamover.util.SqlVarUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

@Component
@Scope("prototype")
public class DataMoveWorker implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(DataMoveWorker.class);

	private static Map<Integer, DataMoveBroadcaster> broadcasters = Collections.synchronizedMap(new HashMap<>());

	private static Map<Integer, Thread> threads = Collections.synchronizedMap(new HashMap<>());

	private Integer instId;//运行实例ID

	private Integer configId;//配置ID

	private DataMoveBroadcaster broadcaster = new DataMoveBroadcaster();//事件广播

	@Autowired
	private DataMoverInstService dataMoverInstService;

	@Autowired
	private DataMoverConfigService dataMoverConfigService;

	@Autowired
	private DataMoverService dataMoverService;

	public void setInstId(Integer instId) {
		this.instId = instId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	@Override
	public void run() {
		try {
			broadcasters.put(instId, broadcaster);
			threads.put(instId, Thread.currentThread());
			DataMoverConfigDetail config = getConfigDetail();
			if (config == null) {
				String message = "迁移配置[ " + configId + " ]不存在";
				logger.warn(message);
				finishTask(Constant.STATUS_TASK_FAIL, message, config);
				return;
			}
			startMonitor(config);
			process(config);
		} catch(Throwable t) {//保证未处理异常的日志输出
			logger.error("instIdID：" + instId + ", 配置ID：" + configId + ", 错误描述：" + t.getMessage(), t);
		} finally {
			broadcasters.remove(instId);
			threads.remove(instId);
		}
	}

	/**
	 * 并不一定能真正停止数据库正在执行的SQL
	 */
	public static void stop(Integer instId) {
		DataMoveBroadcaster broadcaster = broadcasters.get(instId);
		if (broadcaster != null) {
			broadcaster.setStoped(true);
		}
		ConnectionHolder.kill(threads.get(instId));
	}

	private DataMoverConfigDetail getConfigDetail() {
		return dataMoverConfigService.getDetail(configId);
	}

	private void startMonitor(DataMoverConfigDetail config) {//任务超时监听
		if (config.getTimeout() == null || config.getTimeout() <= 0) {
			logger.info("迁移策略[ " + config.getId() + " ]的超时时间小于等于0或未设置，忽略超时线程设置.");
			return;
		}
		Thread t = new Thread(new Runnable() {//监听超时

			@Override
			public void run() {
				try {
					Thread.sleep(config.getTimeout() * 1000);
				} catch (InterruptedException e) {
				}
				broadcaster.setTimeout(true);
				try {
					finishTask(Constant.STATUS_TASK_TIMEOUT, "任务超时", config);
				} catch (Exception e) {
					logger.warn(e.getMessage(), e);
				}
			}

		});
		t.setDaemon(true);
		t.start();
	}

	private void process(DataMoverConfigDetail config) {
		Integer status = null;
		String message = null;
		if (config.getStatus() == Constant.STATUS_DISABLE) {
			status = Constant.STATUS_TASK_FAIL;
			message = "迁移配置[ " + config.getId() + " ]不可用";
			logger.info(message);
		} else if (config.getSrcSql() == null || config.getSrcSql().isEmpty()) {
			status = Constant.STATUS_TASK_FAIL;
			message = "迁移配置[ " + config.getId() + " ]的查询SQL为空";
			logger.warn(message);
		} else {
			try {
				validateDataSource(config);
				ConfigRunningData data = getRunningData(config);
				startTask(data);
				message = execTask(config, data);
				status = Constant.STATUS_TASK_SUCCESS;
				logger.info("迁移配置[ " + config.getId() + " ]执行成功");
			} catch (Exception e) {
				status = Constant.STATUS_TASK_FAIL;
				message = e.getMessage();
				logger.warn(message, e);
			}
		}
		finishTask(status, message, config);
	}

	private void validateDataSource(DataMoverConfigDetail config) {//检查是否单例运行
		if (config.getSrcDs() == null) {
			throw new IllegalStateException("迁移配置[ " + config.getId() + " ]源库配置[ " + config.getSrcDsId() + " ]不存在");
		}
		if (config.getSrcDs().getStatus() == Constant.STATUS_DISABLE) {
			throw new IllegalStateException("迁移配置[ " + config.getId() + " ]源库配置[ " + config.getSrcDsId() + " ]状态为不可用");
		}
		if (Constant.SQL_TYPE_SELECT.equals(config.getSrcSqlType())) {
			if (config.getDestDs() == null) {
				throw new IllegalStateException("迁移配置[ " + config.getId() + " ]目标库配置[ " + config.getDestDsId() + " ]不存在");
			}
			if (config.getDestDs().getStatus() == Constant.STATUS_DISABLE) {
				throw new IllegalStateException("迁移配置[ " + config.getId() + " ]目标库配置[ " + config.getDestDsId() + " ]状态为不可用");
			}
		}
	}

	private ConfigRunningData getRunningData(DataMoverConfigDetail config) {
		ConfigRunningData data = new ConfigRunningData();
		Map<String, Object> vars = new HashMap<>();
		if (config.getVars() != null) {
			for (DataMoverConfigVar var: config.getVars()) {
				if (Constant.VAR_TYPE_CUSTOM == var.getType()) {//处理自定义变量
					vars.put(var.getName(), var.getValue());
				} else {
					logger.warn("变量[ " + var.getId() + " ]的类型[ " + var.getType() + " ]暂不支持。");
				}
			}
		}
		vars.putAll(getInnerVarMap(config));
		String sql = SqlVarUtil.replaceVars(config.getSrcSql(), vars);
		logger.debug("迁移配置[ " + config.getId() + " ]变量替换后的源SQL: " + sql);
		data.setSql(sql);
		data.setVars(vars);
		return data;
	}

	private synchronized void startTask(ConfigRunningData data) {//启动任务
		DataMoverInst tmp = dataMoverInstService.get(instId);
		if (tmp.getStatus() == Constant.STATUS_TASK_READY) {
			Timestamp now = new Timestamp(System.currentTimeMillis());
			tmp.setStatus(Constant.STATUS_TASK_RUNNING);
			if (data != null) {
				tmp.setData(JSON.toJSONString(data));
			}
			tmp.setStartTime(now);
			tmp.setUpdateTime(now);
			dataMoverInstService.update(tmp);
		} else {
			throw new IllegalStateException("迁移配置任务实例[ " + instId + " ]的状态不为就绪[ " + Constant.STATUS_TASK_READY + " ]，无法启动.");
		}
	}

	private String execTask(DataMoverConfigDetail config, ConfigRunningData data) {//开始执行迁移SQL
		try {
			if (Constant.SQL_TYPE_SELECT.equals(config.getSrcSqlType())) {
				List<Map<String, Object>> list = null;
				if (DynamicDataSourceContextHolder.containsKey(config.getSrcDsId())) {
					DynamicDataSourceContextHolder.setKey(config.getSrcDsId());
					list = dataMoverService.srcList(data.getSql(), broadcaster);
				} else {
					throw new NullPointerException("数据源[ " + config.getSrcDsId() + " ]未加载");
				}
				if (DynamicDataSourceContextHolder.containsKey(config.getDestDsId())) {
					DynamicDataSourceContextHolder.setKey(config.getDestDsId());
					return dataMoverService.destUpdate(list, config, broadcaster);
				} else {
					throw new NullPointerException("数据源[ " + config.getDestDsId() + " ]未加载");
				}
			} else if (Constant.SQL_TYPE_UPDATE.equals(config.getSrcSqlType())) {
				if (DynamicDataSourceContextHolder.containsKey(config.getSrcDsId())) {
					DynamicDataSourceContextHolder.setKey(config.getSrcDsId());
					return dataMoverService.srcUpdate(data.getSql(), broadcaster);
				} else {
					throw new NullPointerException("数据源[ " + config.getSrcDsId() + " ]未加载");
				}
			} else {
				throw new IllegalArgumentException("源SQL类型[ " + config.getSrcSqlType() + " ]不合法");
			}
		} finally {
			DynamicDataSourceContextHolder.clearKey();
		}
	}

	private void finishTask(Integer status, String message, DataMoverConfigDetail config) {//设置任务完成
		Integer finishedStatus = null;
		synchronized(this) {
			DataMoverInst tmp = dataMoverInstService.get(instId);
			if (tmp.getStatus() == Constant.STATUS_TASK_READY
					|| tmp.getStatus() == Constant.STATUS_TASK_RUNNING) {//只有就绪或运行中的任务可设置完成
				Timestamp now = new Timestamp(System.currentTimeMillis());
				if (tmp.getStartTime() == null) {//任务可能未开始就出现异常终止
					tmp.setStartTime(now);
				}
				tmp.setStatus(status);
				tmp.setMessage(processTaskMessage(message));
				tmp.setEndTime(now);
				tmp.setUpdateTime(now);
				dataMoverInstService.update(tmp);
				finishedStatus = tmp.getStatus();
			} else if (status != Constant.STATUS_TASK_TIMEOUT) {
				appendInstMessage(message);
			}
		}
		if (finishedStatus != null && !broadcaster.isTimeout() && !broadcaster.isStoped()) {
			afterFinished(finishedStatus, config);
		}
	}

	private void afterFinished(Integer finishedStatus, DataMoverConfigDetail config) {//完成之后执行
		if (config.getPostAction() == Constant.POST_ACTION_NONE) {
			return;
		}
		if (config.getPostData() == null) {
			return;
		}
		if (!(config.getPostCondition() == Constant.POST_CONDITION_ALWAYS//无条件执行
				|| config.getPostCondition() == Constant.POST_CONDITION_SUCCESS//成功时执行
						&& finishedStatus == Constant.STATUS_TASK_SUCCESS
				|| config.getPostCondition() == Constant.POST_CONDITION_FAIL//失败时执行
						&& finishedStatus == Constant.STATUS_TASK_FAIL)) {
			return;
		}
		if (config.getPostAction() == Constant.POST_ACTION_SHELL) {
			//shell脚本中存在\r可能会导致执行报错
			postExecShell(finishedStatus == Constant.STATUS_TASK_SUCCESS, config.getPostData()
					.replaceAll("\r\n", "\n").replaceAll("\r", "\n"));
		} else {
			String message = "完成后执行动作[ " + config.getPostAction() + " ]未实现";
			logger.error(message);
			appendInstMessage(message);
		}
	}

	private void postExecShell(Boolean success, String script) {
		if (script == null || script.trim().isEmpty()) {
			logger.error("SHELL脚本为空，忽略完成后执行动作");
		}
		String configId = "" + this.configId;
		String taskId = "" + this.instId;
		Map<String, String> envs = new HashMap<>();
		envs.put("CONFIG_ID", configId);
		envs.put("TASK_ID", taskId);
		envs.put("TASK_SUCCESS", success.toString());
		Thread t = new Thread(new ShellExecutor(taskId, script, envs, new ShellExecutor.Callback() {

			@Override
			public void onSuccess(String output) {
				logger.info("迁移配置实例[ " + instId + " ]脚本执行成功");
				logger.debug("迁移配置实例[ " + instId + " ]脚本正常输出：" + output);
				StringBuilder sb = new StringBuilder();
				sb.append("### 完成后执行SHELL脚本的正常输出 ###\n").append(output);
				appendInstMessage(sb.toString());
			}

			@Override
			public void onError(String output, String errorMessage) {
				logger.warn("迁移配置实例[ " + instId + " ]脚本执行出错，异常输出：" + errorMessage);
				StringBuilder sb = new StringBuilder();
				sb.append("### 完成后执行SHELL脚本的正常输出 ###\n").append(output).append("\n");
				sb.append("### 完成后执行SHELL脚本的异常输出 ###\n").append(errorMessage);
				appendInstMessage(sb.toString());
			}

			@Override
			public void onException(Exception e) {
				logger.error(e.getMessage(), e);
				StringBuilder sb = new StringBuilder();
				sb.append("### 完成后执行SHELL脚本出现系统异常 ###\n").append(e.getMessage());
				appendInstMessage(sb.toString());
			}

		}));
		t.start();
	}

	private synchronized void appendInstMessage(String message) {
		DataMoverInst inst = dataMoverInstService.get(instId);
		if (inst == null) {
			logger.warn("配置实例[ " + instId + " ]不存在");
			return;
		}
		if (inst.getMessage() != null && !inst.getMessage().contains(message)) {
			StringBuilder sb = new StringBuilder();
			sb.append(inst.getMessage());
			sb.append(inst.getMessage().isEmpty() ? "" : "\n\n");
			sb.append(message);
			message = sb.toString();
		}
		DataMoverInst tmp = new DataMoverInst();
		tmp.setId(instId);
		tmp.setMessage(processTaskMessage(message));
		tmp.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		dataMoverInstService.update(tmp);
	}

	/**
	 * 内置变量
	 */
	private Map<String, Object> getInnerVarMap(DataMoverConfigDetail config) {
		Map<String, Object> map = new HashMap<>();
		map.put("CONFIG_ID", config.getId());
		map.put("CONFIG_NAME", config.getName());
		map.put("CONFIG_REMARK", config.getRemark());
		map.put("CONFIG_INST_ID", instId);
		map.put("SRC_DS_NAME", config.getSrcDs().getName());
		map.put("SRC_DS_USER", config.getSrcDs().getJdbcUsername());
		map.put("DEST_DS_NAME", config.getDestDs() != null ? config.getDestDs().getName() : null);
		map.put("DEST_DS_USER", config.getDestDs() != null ? config.getDestDs().getJdbcUsername() : null);
		map.put("DEST_DS_TABLE", config.getDestTable());
		map.put("DEST_DS_TABLE_PK_JSON", config.getPrimaryKeyListJson());
		map.put("SYSTEM_CURRENT_TIME", System.currentTimeMillis());
		return map;
	}

	private String processTaskMessage(String message) {
		int length = 10000;
		if (message != null && message.length() > length) {
			String ellipsis = " ...";
			return message.substring(0, length - ellipsis.length()) + ellipsis;
		}
		return message;
	}

}
