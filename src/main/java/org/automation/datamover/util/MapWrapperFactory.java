package org.automation.datamover.util;

import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

public class MapWrapperFactory implements ObjectWrapperFactory {

	@Override
	public boolean hasWrapperFor(Object object) {
		return object != null && object instanceof Map;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
		return new MapKeyUpperWrapper(metaObject, (Map) object);
	}

	public static class MapKeyUpperWrapper extends MapWrapper {

		public MapKeyUpperWrapper(MetaObject metaObject, Map<String, Object> map) {
			super(metaObject, map);
		}

		@Override
		public String findProperty(String name, boolean useCamelCaseMapping) {
			return name == null ? null : name.toUpperCase();
		}

	}

}
