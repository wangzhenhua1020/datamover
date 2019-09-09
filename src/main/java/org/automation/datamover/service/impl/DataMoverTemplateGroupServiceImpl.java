package org.automation.datamover.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.automation.datamover.bean.db.DataMoverTemplateGroup;
import org.automation.datamover.bean.resp.DataMoverTemplateSqlVO;
import org.automation.datamover.mapper.DataMoverTemplateGroupMapper;
import org.automation.datamover.service.DataMoverTemplateGroupService;
import org.automation.datamover.service.DataMoverTemplateSqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class DataMoverTemplateGroupServiceImpl implements DataMoverTemplateGroupService {

	@Autowired
	private DataMoverTemplateGroupMapper dataMoverTemplateGroupMapper;

	@Autowired
	private DataMoverTemplateSqlService dataMoverTemplateSqlService;

	@Override
	public List<DataMoverTemplateGroup> list() {
		return dataMoverTemplateGroupMapper.list();
	}

	@Override
	@Transactional
	public void add(DataMoverTemplateGroup group) {
		Assert.isTrue(group != null, "模板分组对象不能为空");
		Timestamp now = new Timestamp(System.currentTimeMillis());
		group.setCreateTime(now);
		group.setUpdateTime(now);
		dataMoverTemplateGroupMapper.add(group);
		if (group.getParentId() != null) {
			DataMoverTemplateGroup parentGroup = dataMoverTemplateGroupMapper.get(group.getParentId());
			Assert.isTrue(parentGroup != null, "模板父分组[ " + group.getParentId() + " ]对象不存在");
			String path = parentGroup.getPath() + group.getId() + "/";
			group.setPath(path);
			dataMoverTemplateGroupMapper.update(group);
		} else {
			String path = "/" + group.getId() + "/";
			group.setPath(path);
			dataMoverTemplateGroupMapper.update(group);
		}
	}

	@Override
	@Transactional
	public void update(DataMoverTemplateGroup group) {
		Assert.isTrue(group != null, "模板分组对象不能为空");
		Assert.isTrue(group.getId() != null, "模板分组ID不能为空");
		group.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		if (group.getParentId() != null) {
			DataMoverTemplateGroup parentGroup = dataMoverTemplateGroupMapper.get(group.getParentId());
			Assert.isTrue(parentGroup != null, "模板父分组[ " + group.getParentId() + " ]对象不存在");
			String path = parentGroup.getPath() + group.getId() + "/";
			group.setPath(path);
			dataMoverTemplateGroupMapper.update(group);
		} else {
			String path = "/" + group.getId() + "/";
			group.setPath(path);
			dataMoverTemplateGroupMapper.update(group);
		}
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		Assert.isTrue(id != null, "模板分组对象ID不能为空");
		DataMoverTemplateGroup group = dataMoverTemplateGroupMapper.get(id);
		Assert.isTrue(group != null, "模板分组[ " + id + " ]对象不存在");
		List<DataMoverTemplateGroup> children = dataMoverTemplateGroupMapper.listChildren(group.getId());
		Assert.isTrue(children.isEmpty(), "模板分组中存在子分组");
		List<DataMoverTemplateSqlVO> templates = dataMoverTemplateSqlService.listByGroup(group.getPath());
		Assert.isTrue(templates.isEmpty(), "模板分组中存在模板对象");
		dataMoverTemplateGroupMapper.delete(id);
	}

}
