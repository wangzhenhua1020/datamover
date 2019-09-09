package org.automation.datamover.bean.resp;

import org.automation.datamover.bean.db.DataMoverTemplateSql;

public class DataMoverTemplateSqlVO extends DataMoverTemplateSql {

	private String groupName;

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
