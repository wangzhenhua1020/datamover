package org.automation.datamover.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.automation.datamover.bean.db.DataMoverTemplateSql;
import org.automation.datamover.bean.req.DataMoverTemplateGroupListQO;
import org.automation.datamover.bean.resp.DataMoverTemplateSqlVO;
import org.automation.datamover.mapper.DataMoverTemplateSqlMapper;
import org.automation.datamover.service.DataMoverTemplateSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class DataMoverTemplateSqlServiceImpl implements DataMoverTemplateSqlService {

	@Autowired
	private DataMoverTemplateSqlMapper dataMoverTemplateSqlMapper;

	@Override
	public PageInfo<DataMoverTemplateSqlVO> listByPage(DataMoverTemplateGroupListQO qo) {
		PageHelper.startPage(qo.getPageNo(), qo.getLimit());
		return new PageInfo<DataMoverTemplateSqlVO>(list(qo));
	}

	@Override
	public List<DataMoverTemplateSqlVO> listByGroup(String groupPath) {
		DataMoverTemplateGroupListQO qo = new DataMoverTemplateGroupListQO();
		qo.setGroupPath(groupPath);
		return list(qo);
	}

	@Override
	public List<DataMoverTemplateSqlVO> listByGroup(Integer groupId) {
		DataMoverTemplateGroupListQO qo = new DataMoverTemplateGroupListQO();
		qo.setGroupId(groupId);
		return list(qo);
	}

	@Override
	@Transactional
	public void add(DataMoverTemplateSql template) {
		Assert.isTrue(template != null, "SQL模板对象不能为空");
		Timestamp now = new Timestamp(System.currentTimeMillis());
		template.setCreateTime(now);
		template.setUpdateTime(now);
		dataMoverTemplateSqlMapper.add(template);
	}

	@Override
	@Transactional
	public void update(DataMoverTemplateSql template) {
		Assert.isTrue(template != null, "SQL模板对象不能为空");
		template.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		dataMoverTemplateSqlMapper.update(template);
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		Assert.isTrue(id != null, "SQL模板对象ID不能为空");
		dataMoverTemplateSqlMapper.delete(id);
	}

	private List<DataMoverTemplateSqlVO> list(DataMoverTemplateGroupListQO qo) {
		if (qo.getName() != null && !qo.getName().isEmpty()) {
			qo.setName("%" + qo.getName() + "%");
		}
		if (qo.getGroupPath() != null && !qo.getGroupPath().isEmpty()) {
			qo.setGroupPath(qo.getGroupPath() + "%");
		}
		return dataMoverTemplateSqlMapper.list(qo);
	}

}
