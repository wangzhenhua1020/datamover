package org.automation.datamover.service;

import java.util.List;

import org.automation.datamover.bean.db.DataMoverInst;
import org.automation.datamover.bean.req.DataMoverInstListQO;
import org.automation.datamover.bean.resp.DataMoverInstVO;

import com.github.pagehelper.PageInfo;

public interface DataMoverInstService {

	PageInfo<DataMoverInstVO> listByPage(DataMoverInstListQO qo);

	List<DataMoverInst> listUnfinished(Integer configId, Integer excludeId);

	DataMoverInst get(Integer id);

	void add(DataMoverInst inst);

	void update(DataMoverInst inst);

	void stop(Integer id);

}
