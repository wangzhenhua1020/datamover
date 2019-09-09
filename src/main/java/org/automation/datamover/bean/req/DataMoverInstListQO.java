package org.automation.datamover.bean.req;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class DataMoverInstListQO extends PageForm {

	private Integer configId;

	private String configName;

	private Integer triggerType;

	private Integer status;

	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	private Date startTime;

	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	private Date endTime;

	public Integer getConfigId() {
		return configId;
	}

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public Integer getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(Integer triggerType) {
		this.triggerType = triggerType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
