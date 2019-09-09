package org.automation.datamover.bean.req;

/**
 * 前台分页请求参数接收实体
 */
public class PageForm {

	private int offset;

	private int limit;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	//根据offset及limit计算当前页码
	public int getPageNo() {
		if (limit <= 0) {
			return 1;
		}
		return offset / limit + 1;
	}

}
