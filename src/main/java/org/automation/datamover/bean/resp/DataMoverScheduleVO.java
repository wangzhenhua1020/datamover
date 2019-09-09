package org.automation.datamover.bean.resp;

import org.automation.datamover.bean.db.DataMoverSchedule;

public class DataMoverScheduleVO extends DataMoverSchedule {

	private String configName;

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

}
