package org.automation.datamover.service;

import java.util.List;

import org.automation.datamover.bean.db.DataMoverConfig;
import org.automation.datamover.bean.ext.DataMoverConfigDetail;
import org.automation.datamover.bean.req.DataMoverConfigListQO;
import org.automation.datamover.bean.resp.DataMoverConfigVO;

import com.github.pagehelper.PageInfo;

public interface DataMoverConfigService {

	PageInfo<DataMoverConfigVO> listByPage(DataMoverConfigListQO qo);

	List<DataMoverConfigVO> list(DataMoverConfigListQO qo);

	DataMoverConfig get(Integer id);

	DataMoverConfigDetail getDetail(Integer id);

	void add(DataMoverConfigDetail config);

	void update(DataMoverConfigDetail config);

	void delete(Integer id);

	Integer exec(Integer id, Integer triggerType);

}
