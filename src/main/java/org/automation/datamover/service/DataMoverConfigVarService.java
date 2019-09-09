package org.automation.datamover.service;

import java.util.List;

import org.automation.datamover.bean.db.DataMoverConfigVar;

public interface DataMoverConfigVarService {

	List<DataMoverConfigVar> list(Integer configId);

	void add(DataMoverConfigVar var);

	void update(DataMoverConfigVar var);

	void delete(Integer id);

	void deleteByConfig(Integer configId);

}
