package org.automation.datamover.bean.req;

public class DataMoverTemplateGroupListQO extends PageForm {

	private String name;

	private Integer groupId;

	private String groupPath;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getGroupPath() {
		return groupPath;
	}

	public void setGroupPath(String groupPath) {
		this.groupPath = groupPath;
	}

}
