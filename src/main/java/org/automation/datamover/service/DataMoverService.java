package org.automation.datamover.service;

import java.util.List;
import java.util.Map;

import org.automation.datamover.bean.ext.DataMoverConfigDetail;
import org.automation.datamover.service.worker.DataMoveBroadcaster;

/**
 * 数据迁移实现
 */
public interface DataMoverService {

	/**
	 * 源库目标库相同时，可能通过一条SQL直接将查出结果插入到目标库
	 * 该方法执行类似于insert into select的语句
	*/
	String srcUpdate(String sql, DataMoveBroadcaster broadcaster);

	/**
	 * 将数据由源库查出形成list
	 */
	List<Map<String, Object>> srcList(String sql, DataMoveBroadcaster broadcaster);

	/**
	 * 更新目标库
	 */
	String destUpdate(List<Map<String, Object>> list, DataMoverConfigDetail config, DataMoveBroadcaster broadcaster);

}
