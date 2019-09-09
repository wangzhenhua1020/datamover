package org.automation.datamover.service;

import java.util.List;

import org.automation.datamover.bean.db.DataMoverTemplateGroup;

public interface DataMoverTemplateGroupService {

	List<DataMoverTemplateGroup> list();

	void add(DataMoverTemplateGroup group);

	void update(DataMoverTemplateGroup group);

	void delete(Integer id);

}
