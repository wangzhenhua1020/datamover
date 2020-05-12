package org.automation.datamover.service.impl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.automation.datamover.bean.Constant;
import org.automation.datamover.bean.db.DataMoverSchedule;
import org.automation.datamover.bean.req.PageForm;
import org.automation.datamover.bean.resp.DataMoverScheduleVO;
import org.automation.datamover.mapper.DataMoverScheduleMapper;
import org.automation.datamover.service.DataMoverConfigService;
import org.automation.datamover.service.DataMoverScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class DataMoverScheduleServiceImpl implements DataMoverScheduleService {

	private static Logger logger = LoggerFactory.getLogger(DataMoverScheduleServiceImpl.class);

	@Autowired
	private ThreadPoolTaskScheduler threadPoolTaskScheduler;

	@Autowired
	private DataMoverScheduleMapper dataMoverScheduleMapper;

	@Autowired
	private DataMoverConfigService dataMoverConfigService;

	private Map<Integer, ScheduledFuture<?>> futures = Collections.synchronizedMap(new HashMap<>());

	@Override
	public PageInfo<DataMoverScheduleVO> listByPage(PageForm pageForm) {
		PageHelper.startPage(pageForm.getPageNo(), pageForm.getLimit());
		return new PageInfo<DataMoverScheduleVO>(dataMoverScheduleMapper.list4vo());
	}

	@Override
	public void reload(Integer scheduleId) {
		stop(scheduleId);
		start(scheduleId);
	}

	@Override
	public void reload() {
		stopAll();
		startAll();
	}

	public DataMoverSchedule get(Integer id) {
		return dataMoverScheduleMapper.get(id);
	}

	@PostConstruct
	private void startAll() {
		List<DataMoverSchedule> list = dataMoverScheduleMapper.list(Constant.STATUS_AVAILABLE, null);
		for (DataMoverSchedule dataMoverSchedule: list) {
			start(dataMoverSchedule);
		}
	}

	private void stopAll() {
		for (Map.Entry<Integer, ScheduledFuture<?>> entry: futures.entrySet()) {
			if (entry != null && entry.getValue() != null) {
				entry.getValue().cancel(true);
			}
		}
		futures.clear();
	}
	
	private void start(DataMoverSchedule dataMoverSchedule) {
		if (dataMoverSchedule == null) {
			return;
		}
		if (dataMoverSchedule.getStatus() == Constant.STATUS_DISABLE) {
			logger.info("定时调度[ " + dataMoverSchedule.getId() + " ]不可用，忽略任务启动");
			return;
		}
		if (dataMoverSchedule.getExpr() == null || dataMoverSchedule.getExpr().isEmpty()) {
			logger.warn("定时调度[ " + dataMoverSchedule.getId() + " ]表达式为空，忽略任务启动");
			return;
		}
		ScheduledFuture<?> future = threadPoolTaskScheduler.schedule(
			new Runnable() {

				@Override
				public void run() {
					dataMoverConfigService.exec(dataMoverSchedule.getConfigId(), Constant.TASK_TRIGGER_TYPE_SCHEDULE);
				}
				
			},
			new Trigger() {

				@Override
				public Date nextExecutionTime(TriggerContext triggerContext) {
					return new CronTrigger(dataMoverSchedule.getExpr()).nextExecutionTime(triggerContext);
				}

			});
		futures.put(dataMoverSchedule.getId(), future);
	}


	private void start(Integer scheduleId) {
		start(dataMoverScheduleMapper.get(scheduleId));
	}

	private void stop(Integer scheduleId) {
		ScheduledFuture<?> future = futures.get(scheduleId);
		if (future != null) {
			future.cancel(true);
			futures.remove(scheduleId);
		}
	}

	@Override
	@Transactional
	public void add(DataMoverSchedule bean) {
		Assert.isTrue(bean != null, "调度对象不能为空");
		Timestamp now = new Timestamp(System.currentTimeMillis());
		bean.setCreateTime(now);
		bean.setUpdateTime(now);
		dataMoverScheduleMapper.add(bean);
		try {
			start(bean);
		} catch(Exception e) {
			throw new RuntimeException("调度任务无法启动，添加失败：" + e.getMessage(), e);
		}
	}

	@Override
	@Transactional
	public void update(DataMoverSchedule bean) {
		Assert.isTrue(bean != null, "调度对象不能为空");
		bean.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		dataMoverScheduleMapper.update(bean);
		try {
			reload(bean.getId());
		} catch(Exception e) {
			throw new RuntimeException("调度任务无法重新加载，修改失败", e);
		}
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		Assert.isTrue(id != null, "调度对象ID不能为空");
		dataMoverScheduleMapper.delete(id);
		try {
			stop(id);
		} catch(Exception e) {
			throw new RuntimeException("调度任务无法停止，删除失败", e);
		}
	}

	@Override
	@Transactional
	public void changeStatus(Integer scheduleId, Integer status) {
		dataMoverScheduleMapper.changeStatus(scheduleId, status, new Timestamp(System.currentTimeMillis()));
		if (status == Constant.STATUS_AVAILABLE) {
			reload(scheduleId);
		} else {
			stop(scheduleId);
		}
	}

	@Override
	@Transactional
	public void changeStatusByConfig(Integer configId, Integer status) {
		dataMoverScheduleMapper.changeStatusByConfig(configId, status, new Timestamp(System.currentTimeMillis()));
		List<DataMoverSchedule> list = dataMoverScheduleMapper.list(null, configId);
		for (DataMoverSchedule dataMoverSchedule: list) {
			if (status == Constant.STATUS_AVAILABLE) {
				stop(dataMoverSchedule.getId());
				start(dataMoverSchedule);
			} else {
				stop(dataMoverSchedule.getId());
			}
		}
	}

}
