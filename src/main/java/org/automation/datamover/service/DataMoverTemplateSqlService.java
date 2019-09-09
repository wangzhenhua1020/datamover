package org.automation.datamover.service;

import java.util.List;

import org.automation.datamover.bean.db.DataMoverTemplateSql;
import org.automation.datamover.bean.req.DataMoverTemplateGroupListQO;
import org.automation.datamover.bean.resp.DataMoverTemplateSqlVO;

import com.github.pagehelper.PageInfo;

public interface DataMoverTemplateSqlService {

	PageInfo<DataMoverTemplateSqlVO> listByPage(DataMoverTemplateGroupListQO qo);

	List<DataMoverTemplateSqlVO> listByGroup(String groupPath);

	List<DataMoverTemplateSqlVO> listByGroup(Integer groupId);

	void add(DataMoverTemplateSql template);

	void update(DataMoverTemplateSql template);

	void delete(Integer id);

}
