package org.automation.datamover.configuration.db;

public class DynamicDataSourceContextHolder {

	private static final ThreadLocal<Object> contextHolder = new ThreadLocal<>();

	public static synchronized void setKey(Object key) {
		contextHolder.set(key);
	}

	public static Object getKey() {
		return contextHolder.get();
	}

	public static void clearKey() {
		contextHolder.remove();
	}

	public static boolean containsKey(Object key) {
		return DynamicDataSource.getInstance().contains(key);
	}

}