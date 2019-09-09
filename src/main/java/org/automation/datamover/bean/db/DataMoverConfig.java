package org.automation.datamover.bean.db;

import java.sql.Timestamp;

public class DataMoverConfig {

	private Integer id;

	private String name;

	private Integer timeout;

	private Integer srcDsId;

	private Integer srcSqlType;

	private String srcSql;

	private Integer destDsId;

	private String destTable;

	private Integer destTableDeleteType;

	private String primaryKeyListJson;

	private Integer singleton;

	private Integer status;

	private Integer postAction;

	private Integer postCondition;

	private String postData;

	private Timestamp createTime;

	private Timestamp updateTime;

	private String remark;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getSrcDsId() {
		return srcDsId;
	}

	public void setSrcDsId(Integer srcDsId) {
		this.srcDsId = srcDsId;
	}

	public Integer getSrcSqlType() {
		return srcSqlType;
	}

	public void setSrcSqlType(Integer srcSqlType) {
		this.srcSqlType = srcSqlType;
	}

	public String getSrcSql() {
		return srcSql;
	}

	public void setSrcSql(String srcSql) {
		this.srcSql = srcSql;
	}

	public Integer getDestDsId() {
		return destDsId;
	}

	public void setDestDsId(Integer destDsId) {
		this.destDsId = destDsId;
	}

	public String getDestTable() {
		return destTable;
	}

	public void setDestTable(String destTable) {
		this.destTable = destTable;
	}

	public Integer getDestTableDeleteType() {
		return destTableDeleteType;
	}

	public void setDestTableDeleteType(Integer destTableDeleteType) {
		this.destTableDeleteType = destTableDeleteType;
	}

	public String getPrimaryKeyListJson() {
		return primaryKeyListJson;
	}

	public void setPrimaryKeyListJson(String primaryKeyListJson) {
		this.primaryKeyListJson = primaryKeyListJson;
	}

	public Integer getSingleton() {
		return singleton;
	}

	public void setSingleton(Integer singleton) {
		this.singleton = singleton;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPostAction() {
		return postAction;
	}

	public void setPostAction(Integer postAction) {
		this.postAction = postAction;
	}

	public Integer getPostCondition() {
		return postCondition;
	}

	public void setPostCondition(Integer postCondition) {
		this.postCondition = postCondition;
	}

	public String getPostData() {
		return postData;
	}

	public void setPostData(String postData) {
		this.postData = postData;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
