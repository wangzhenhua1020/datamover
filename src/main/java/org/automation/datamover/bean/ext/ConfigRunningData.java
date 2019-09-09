package org.automation.datamover.bean.ext;

import java.util.Map;

public class ConfigRunningData {

	private String sql;

	private Map<String, Object> vars;

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Map<String, Object> getVars() {
		return vars;
	}

	public void setVars(Map<String, Object> vars) {
		this.vars = vars;
	}

}
