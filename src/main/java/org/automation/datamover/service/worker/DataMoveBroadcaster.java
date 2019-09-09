package org.automation.datamover.service.worker;

/**
 * 数据迁移事件广播
 */
public class DataMoveBroadcaster {

	//是否超时
	private boolean timeout;

	//是否停止
	private boolean stoped;

	public boolean isTimeout() {
		return timeout;
	}

	public void setTimeout(boolean timeou) {
		this.timeout = timeou;
	}

	public boolean isStoped() {
		return stoped;
	}

	public void setStoped(boolean stoped) {
		this.stoped = stoped;
	}

}
