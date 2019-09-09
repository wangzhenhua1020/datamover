package org.automation.datamover.bean.ext;

import java.util.List;

import org.automation.datamover.bean.db.DataMoverConfig;
import org.automation.datamover.bean.db.DataMoverConfigVar;
import org.automation.datamover.bean.db.DataMoverDataSource;

public class DataMoverConfigDetail extends DataMoverConfig {

	private DataMoverDataSource srcDs;

	private DataMoverDataSource destDs;

	private List<DataMoverConfigVar> vars;

	public DataMoverDataSource getSrcDs() {
		return srcDs;
	}

	public void setSrcDs(DataMoverDataSource srcDs) {
		this.srcDs = srcDs;
	}

	public DataMoverDataSource getDestDs() {
		return destDs;
	}

	public void setDestDs(DataMoverDataSource destDs) {
		this.destDs = destDs;
	}

	public List<DataMoverConfigVar> getVars() {
		return vars;
	}

	public void setVars(List<DataMoverConfigVar> vars) {
		this.vars = vars;
	}

}
