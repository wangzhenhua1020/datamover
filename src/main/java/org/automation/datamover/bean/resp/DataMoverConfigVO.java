package org.automation.datamover.bean.resp;

import org.automation.datamover.bean.db.DataMoverConfig;

public class DataMoverConfigVO extends DataMoverConfig {

	private String srcDsName;

	private String destDsName;

	public String getSrcDsName() {
		return srcDsName;
	}

	public void setSrcDsName(String srcDsName) {
		this.srcDsName = srcDsName;
	}

	public String getDestDsName() {
		return destDsName;
	}

	public void setDestDsName(String destDsName) {
		this.destDsName = destDsName;
	}

}
