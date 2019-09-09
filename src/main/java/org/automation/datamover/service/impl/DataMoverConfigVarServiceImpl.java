package org.automation.datamover.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.automation.datamover.bean.db.DataMoverConfigVar;
import org.automation.datamover.mapper.DataMoverConfigVarMapper;
import org.automation.datamover.service.DataMoverConfigVarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataMoverConfigVarServiceImpl implements DataMoverConfigVarService {

	@Autowired
	private DataMoverConfigVarMapper dataMoverConfigVarMapper;

	@Override
	public List<DataMoverConfigVar> list(Integer configId) {
		return dataMoverConfigVarMapper.list(configId);
	}

	@Override
	public void add(DataMoverConfigVar var) {
		var.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		dataMoverConfigVarMapper.add(var);
	}

	@Override
	public void update(DataMoverConfigVar var) {
		var.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		dataMoverConfigVarMapper.update(var);
	}

	@Override
	public void delete(Integer id) {
		dataMoverConfigVarMapper.delete(id);
	}

	@Override
	public void deleteByConfig(Integer configId) {
		dataMoverConfigVarMapper.deleteByConfig(configId);
	}

}
