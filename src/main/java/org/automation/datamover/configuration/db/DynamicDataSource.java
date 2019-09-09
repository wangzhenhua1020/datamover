package org.automation.datamover.configuration.db;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;


public class DynamicDataSource extends AbstractRoutingDataSource {
	
	private static DynamicDataSource instance;

	private Set<Object> dsKeys = new HashSet<>();

	public static synchronized DynamicDataSource getInstance() {
		if (instance == null) {
			instance = new DynamicDataSource();
		}
		return instance;
	}

	@Override
	public synchronized void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);
		// 必须添加该句，让方法根据重新赋值的targetDataSource依次根据key关键字
		// 查找数据源,返回DataSource,否则新添加数据源无法识别到
		super.afterPropertiesSet();
		dsKeys.clear();
		if (targetDataSources != null) {
			dsKeys.addAll(targetDataSources.keySet());
		}
	}

	// 实现其抽象方法,
	// 因为在创建DataSource这个方法:determineTargetDataSource()中(上面有分析)
	// 会调用这个key关键字,根据这个key在重新赋值的targetDataSource里面找DataSource
	protected Object determineCurrentLookupKey() {
		return DynamicDataSourceContextHolder.getKey();
	}

	/**
	 * 是否包含指定KEY的数据源
	 */
	protected boolean contains(Object key) {
		return dsKeys.contains(key);
	}

}