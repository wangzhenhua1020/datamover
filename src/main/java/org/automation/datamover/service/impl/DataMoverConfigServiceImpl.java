package org.automation.datamover.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.automation.datamover.bean.Constant;
import org.automation.datamover.bean.db.DataMoverConfig;
import org.automation.datamover.bean.db.DataMoverConfigVar;
import org.automation.datamover.bean.db.DataMoverInst;
import org.automation.datamover.bean.ext.DataMoverConfigDetail;
import org.automation.datamover.bean.req.DataMoverConfigListQO;
import org.automation.datamover.bean.resp.DataMoverConfigVO;
import org.automation.datamover.mapper.DataMoverConfigMapper;
import org.automation.datamover.service.DataMoverConfigService;
import org.automation.datamover.service.DataMoverConfigVarService;
import org.automation.datamover.service.DataMoverDataSourceService;
import org.automation.datamover.service.DataMoverInstService;
import org.automation.datamover.service.DataMoverScheduleService;
import org.automation.datamover.service.worker.DataMoveWorker;
import org.automation.datamover.util.DataCompareUtil;
import org.automation.datamover.util.DataCompareUtil.DataChangeEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class DataMoverConfigServiceImpl implements DataMoverConfigService {

	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;

	@Autowired
	private ApplicationContext  applicationContext;

	@Autowired
	private DataMoverConfigMapper dataMoverConfigMapper;

	@Autowired
	private DataMoverConfigVarService dataMoverConfigVarService;

	@Autowired
	private DataMoverScheduleService dataMoverScheduleService;

	@Autowired
	private DataMoverInstService dataMoverInstService;

	@Autowired
	private DataMoverDataSourceService dataMoverDataSourceService;

	@Override
	public PageInfo<DataMoverConfigVO> listByPage(DataMoverConfigListQO qo) {
		PageHelper.startPage(qo.getPageNo(), qo.getLimit());
		return new PageInfo<DataMoverConfigVO>(list(qo));
	}

	@Override
	public List<DataMoverConfigVO> list(DataMoverConfigListQO qo) {
		if (qo.getName() != null && !qo.getName().isEmpty()) {
			qo.setName("%" + qo.getName() + "%");
		}
		if (qo.getSrcDsName() != null && !qo.getSrcDsName().isEmpty()) {
			qo.setSrcDsName("%" + qo.getSrcDsName() + "%");
		}
		if (qo.getDestDsName() != null && !qo.getDestDsName().isEmpty()) {
			qo.setDestDsName("%" + qo.getDestDsName() + "%");
		}
		return dataMoverConfigMapper.list4vo(qo);
	}

	@Override
	public DataMoverConfig get(Integer id) {
		return dataMoverConfigMapper.get(id);
	}

	@Override
	public DataMoverConfigDetail getDetail(Integer id) {
		DataMoverConfig config = this.get(id);
		if (config == null) {
			return null;
		}
		DataMoverConfigDetail detail = new DataMoverConfigDetail();
		BeanUtils.copyProperties(config, detail);
		if (detail.getSrcDsId() != null) {
			detail.setSrcDs(dataMoverDataSourceService.get(detail.getSrcDsId()));
		}
		if (detail.getDestDsId() != null) {
			detail.setDestDs(dataMoverDataSourceService.get(detail.getDestDsId()));
		}
		List<DataMoverConfigVar> vars = dataMoverConfigVarService.list(config.getId());
		detail.setVars(vars);
		return detail;
	}

	@Override
	@Transactional
	public void add(DataMoverConfigDetail config) {
		validateConfig(config);
		Timestamp now = new Timestamp(System.currentTimeMillis());
		config.setCreateTime(now);
		config.setUpdateTime(now);
		dataMoverConfigMapper.add(config);
		if (config.getVars() != null) {
			for (DataMoverConfigVar var: config.getVars()) {
				var.setConfigId(config.getId());
				var.setType(Constant.VAR_TYPE_CUSTOM);//后续增加新的变量类型时，需要前台传递
				dataMoverConfigVarService.add(var);
			}
		}
	}

	@Override
	@Transactional
	public void update(DataMoverConfigDetail config) {
		Assert.isTrue(config != null, "配置不能为空");
		Assert.isTrue(get(config.getId()) != null, "配置不存在");
		validateConfig(config);
		config.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		dataMoverConfigMapper.update(config);
		List<DataMoverConfigVar> dbVars = dataMoverConfigVarService.list(config.getId());
		List<DataMoverConfigVar> formVars = config.getVars();
		if (formVars == null) {
			formVars = new ArrayList<>();
		}
		DataChangeEntity<DataMoverConfigVar> result = DataCompareUtil.compareById(formVars, dbVars, "getId");
		for (DataMoverConfigVar var: result.adds) {
			var.setConfigId(config.getId());
			var.setType(Constant.VAR_TYPE_CUSTOM);//后续增加新的变量类型时，需要前台传递
			dataMoverConfigVarService.add(var);
		}
		for (DataMoverConfigVar var: result.updates) {
			var.setConfigId(config.getId());
			var.setType(Constant.VAR_TYPE_CUSTOM);//后续增加新的变量类型时，需要前台传递
			dataMoverConfigVarService.update(var);
		}
		for (DataMoverConfigVar var: result.deletes) {
			dataMoverConfigVarService.delete(var.getId());
		}
	}

	@Transactional
	public void delete(Integer id) {
		Assert.isTrue(id != null, "配置ID不能为空");
		dataMoverConfigMapper.delete(id);
		dataMoverConfigVarService.deleteByConfig(id);
		dataMoverScheduleService.changeStatusByConfig(id, Constant.STATUS_DISABLE);
	}

	@Override
	public Integer exec(Integer id, Integer triggerType) {
		DataMoverConfig config = dataMoverConfigMapper.get(id);
		Assert.isTrue(config != null, "配置对象不存在");
		DataMoverInst inst = null;
		boolean passed = false;
		synchronized(this) {
			inst = new DataMoverInst();
			Timestamp now = new Timestamp(System.currentTimeMillis());
			inst.setConfigId(id);
			inst.setTriggerType(triggerType);
			inst.setCreateTime(now);
			inst.setUpdateTime(now);
			String message = validateSingleton(config);
			if (message != null) {//单例验证不通过
				inst.setStartTime(now);
				inst.setEndTime(now);
				inst.setStatus(Constant.STATUS_TASK_FAIL);
				inst.setMessage(message);
			} else {
				passed = true;
				inst.setStatus(Constant.STATUS_TASK_READY);
			}
			dataMoverInstService.add(inst);
		}
		if (passed) {
			DataMoveWorker worker = applicationContext.getBean(DataMoveWorker.class);
			worker.setInstId(inst.getId());
			worker.setConfigId(id);
			threadPoolTaskScheduler.execute(worker);
			return inst.getId();
		}
		//未增加事务控制，不会导致事务回滚
		throw new IllegalStateException(inst.getMessage());
	}

	private void validateConfig(DataMoverConfig config) {
		Assert.isTrue(config != null, "配置不能为空");
		Assert.isTrue(config.getName() != null, "配置名称不能为空");
		Assert.isTrue(config.getSrcDsId() != null, "源库不能为空");
		Assert.isTrue(config.getSrcSqlType() != null, "源库SQL类型不能为空");
		Assert.isTrue(config.getSrcSql() != null, "源库SQL不能为空");
		if (config.getSrcSqlType() == Constant.SQL_TYPE_SELECT) {
			Assert.isTrue(config.getDestDsId() != null, "目标库不能为空");
			Assert.isTrue(config.getDestTable() != null, "目标库表不能为空");
			Assert.isTrue(config.getPrimaryKeyListJson() != null, "目标库表主键格式不正确");
			if (config.getPrimaryKeyListJson() != null && config.getPrimaryKeyListJson().isEmpty()) {
				try {
					JSONArray.parseArray(config.getPrimaryKeyListJson());
				} catch(Exception e) {
					throw new IllegalArgumentException("目标库表主键格式不正确");
				}
			}
			Assert.isTrue(config.getDestTableDeleteType() != null, "目标库表删除方式不能为空");
		}
		Assert.isTrue(config.getSingleton() != null, "是否单例不能为空");
		Assert.isTrue(config.getStatus() != null, "是否可用不能为空");
		if (config.getTimeout() != null && config.getTimeout() <= 0) {
			throw new IllegalArgumentException("超时时间必须为正整数");
		}
	}

	private String validateSingleton(DataMoverConfig config) {//检查是否单例运行
		if (config.getSingleton() == Constant.SINGLETON_NO) {
			return null;
		}
		List<DataMoverInst> unfinishedList = dataMoverInstService.listUnfinished(config.getId(), null);//一定时间范围内未完成的任务实例
		StringBuilder unfinishedIds = new StringBuilder();
		for (DataMoverInst inst: unfinishedList) {
			unfinishedIds.append(unfinishedIds.length() > 0 ? "," + inst.getId() : inst.getId());
		}
		if (unfinishedIds.length() > 0) {
			return "迁移配置[ " + config.getId() + " ]存在未完成实例[ " + unfinishedIds + " ]，无法执行";
		}
		return null;
	}

}
