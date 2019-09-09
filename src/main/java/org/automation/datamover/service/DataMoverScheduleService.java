package org.automation.datamover.service;

import org.automation.datamover.bean.db.DataMoverSchedule;
import org.automation.datamover.bean.req.PageForm;
import org.automation.datamover.bean.resp.DataMoverScheduleVO;

import com.github.pagehelper.PageInfo;

public interface DataMoverScheduleService {

	PageInfo<DataMoverScheduleVO> listByPage(PageForm pageForm);

	void reload(Integer id);

	void reload();

	DataMoverSchedule get(Integer id);

	void add(DataMoverSchedule bean);

	void update(DataMoverSchedule bean);

	void delete(Integer id);

	void changeStatus(Integer id, Integer status);

	void changeStatusByConfig(Integer configId, Integer status);

}
