package org.automation.datamover.bean.req;

public class DataMoverConfigListQO extends PageForm {

	private String name;

	private String srcDsName;

	private String destDsName;

	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
