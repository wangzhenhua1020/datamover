package org.automation.datamover.service;

import java.util.List;

import org.automation.datamover.bean.db.DataMoverDataSource;
import org.automation.datamover.bean.req.DataMoverDataSourceListQO;

import com.github.pagehelper.PageInfo;

public interface DataMoverDataSourceService {

	PageInfo<DataMoverDataSource> listByPage(DataMoverDataSourceListQO qo);

	List<DataMoverDataSource> list(DataMoverDataSourceListQO qo);

	DataMoverDataSource get(Integer id);

	void reload();

	void add(DataMoverDataSource bean);

	void update(DataMoverDataSource bean);

	void delete(Integer id);

}
