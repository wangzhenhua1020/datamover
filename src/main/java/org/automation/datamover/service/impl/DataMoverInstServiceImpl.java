package org.automation.datamover.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.automation.datamover.bean.Constant;
import org.automation.datamover.bean.db.DataMoverInst;
import org.automation.datamover.bean.req.DataMoverInstListQO;
import org.automation.datamover.bean.resp.DataMoverInstVO;
import org.automation.datamover.mapper.DataMoverInstMapper;
import org.automation.datamover.service.DataMoverInstService;
import org.automation.datamover.service.worker.DataMoveWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class DataMoverInstServiceImpl implements DataMoverInstService {

	@Autowired
	private DataMoverInstMapper dataMoverInstMapper;

	@Override
	public PageInfo<DataMoverInstVO> listByPage(DataMoverInstListQO qo) {
		PageHelper.startPage(qo.getPageNo(), qo.getLimit());
		if (qo.getConfigName() != null && !qo.getConfigName().isEmpty()) {
			qo.setConfigName("%" + qo.getConfigName() + "%");
		}
		return new PageInfo<DataMoverInstVO>(dataMoverInstMapper.list4vo(qo));
	}

	@Override
	public List<DataMoverInst> listUnfinished(Integer configId, Integer excludeId) {
		List<Integer> excludeIdList = new ArrayList<>();
		if (excludeId != null) {
			excludeIdList.add(excludeId);
		}
		return listUnfinished(configId, excludeIdList);
	}

	@Override
	public DataMoverInst get(Integer id) {
		return dataMoverInstMapper.get(id);
	}

	@Override
	public void add(DataMoverInst inst) {
		Assert.isTrue(inst != null, "配置实例不能为空");
		Timestamp now = new Timestamp(System.currentTimeMillis());
		if (inst.getCreateTime() == null) {
			inst.setCreateTime(now);
		}
		if (inst.getUpdateTime() == null) {
			inst.setUpdateTime(now);
		}
		dataMoverInstMapper.add(inst);
	}

	@Override
	public synchronized void update(DataMoverInst inst) {
		Assert.isTrue(inst != null, "配置实例不能为空");
		if (inst.getUpdateTime() == null) {
			inst.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		}
		dataMoverInstMapper.update(inst);
	}

	@Override
	public synchronized void stop(Integer id) {
		DataMoverInst inst = dataMoverInstMapper.get(id);
		Assert.isTrue(inst != null, "配置实例不存在");
		Assert.isTrue(inst.getStatus() != null && inst
				.getStatus().equals(Constant.STATUS_TASK_RUNNING), "配置实例未运行");
		DataMoveWorker.stop(id);
		inst.setStatus(Constant.STATUS_TASK_FAIL);
		inst.setMessage("手动停止");
		Timestamp now = new Timestamp(System.currentTimeMillis());
		inst.setUpdateTime(now);
		inst.setEndTime(now);
		dataMoverInstMapper.update(inst);
	}

	private List<DataMoverInst> listUnfinished(Integer configId, List<Integer> excludeIdList) {
		List<Integer> unfinishedStatusList = new ArrayList<>();
		unfinishedStatusList.add(Constant.STATUS_TASK_READY);
		unfinishedStatusList.add(Constant.STATUS_TASK_RUNNING);
		//这里认为一个任务执行最多不会超过3天，只查询3天内未完成的任务
		Timestamp earliestTime = new Timestamp(System.currentTimeMillis() - 3 * 24 * 60 * 1000);
		return dataMoverInstMapper.listUnfinished(configId, excludeIdList, unfinishedStatusList, earliestTime);
	}

}
