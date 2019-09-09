package org.automation.datamover.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据对比
 * 
 * 主要用于数据同步，对比源集合及目标集合，将对象分为增加、删除、修改三类并返回
 */
public class DataCompareUtil {

	private DataCompareUtil(){
		super();
	}

	public static class DataChangeEntity<E> {

		public final List<E> adds;
		public final List<E> updates;
		public final List<E> deletes;

		protected DataChangeEntity(List<E> adds, List<E> updates, List<E> deletes) {
			this.adds = adds;
			this.updates = updates;
			this.deletes = deletes;
		}

	}

	/**
	 * 根据ID对比数据
	 * 
	 * @param srcBeans
	 * @param dstBeans
	 * @param idMethodNames 获取ID的方法（最少传递一个方法，传递多个方法代表多列唯一标识一条记录，ID为空时当做新增对象）
	 * @return
	 * @throws Exception
	 */
	public static <E> DataChangeEntity<E> compareById(List<E> srcBeans, List<E> dstBeans,
			String... idMethodNames) {
		Method[] methods = null;
		if (idMethodNames.length == 0) {
			throw new NullPointerException("获取对比字符串的方法名称不能为空");
		}
		List<E> addBeans = new ArrayList<>();
		List<String> srcIds = new ArrayList<>();
		Map<String, E> srcBeanMap = new HashMap<>();
		for (E srcBean: srcBeans) {
			if (methods == null) {
				methods = getMethods(srcBean, idMethodNames);
			}
			String id = getId(srcBean, methods);
			if (id == null || id.isEmpty()) {//ID为空认为新增bean
				addBeans.add(srcBean);
			} else {
				srcIds.add(id);
				srcBeanMap.put(id, srcBean);
			}
		}
		List<String> dstIds = new ArrayList<>();
		Map<String, E> dstBeanMap = new HashMap<>();
		for (E dstBean: dstBeans) {
			if (methods == null) {
				methods = getMethods(dstBean, idMethodNames);
			}
			String id = getId(dstBean, methods);
			dstIds.add(id);
			dstBeanMap.put(id, dstBean);
		}

		//将对象分为三类：增加、删除、修改
		List<E> updateBeans = new ArrayList<>();
		List<E> deleteBeans = new ArrayList<>();
		for (String id: srcIds) {
			if (dstIds.contains(id)) {
				updateBeans.add(srcBeanMap.get(id));
			} else {
				addBeans.add(srcBeanMap.get(id));
			}
		}
		for (String id: dstIds) {
			if (!srcIds.contains(id)) {
				deleteBeans.add(dstBeanMap.get(id));
			}
		}
		return new DataChangeEntity<>(addBeans, updateBeans, deleteBeans);
	}

	private static <E> Method[] getMethods(E bean, String... methodNames) {
		List<Method> list = new ArrayList<>();
		if (methodNames != null && methodNames.length > 0) {
			for (String idMethodName: methodNames) {
				Method method;
				try {
					method = bean.getClass().getMethod(idMethodName);
				} catch (NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
				method.setAccessible(true);
				list.add(method);
			}
		}
		return list.toArray(new Method[0]);
	}

	private static <E> String getId(E bean, Method[] methods) {
		String id = "";
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			try {
				Object partPk = method.invoke(bean);
				id += (partPk != null ? partPk : "") + (i + 1 == methods.length ? "" : Character.toString((char)7));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		return id;
	}

}
