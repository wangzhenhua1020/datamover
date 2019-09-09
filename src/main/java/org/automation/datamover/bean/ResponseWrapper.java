package org.automation.datamover.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注Controller层返回值（返回值不能为void类型）
 *   标注后，会将返回值加工成DataResponse对象
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ResponseWrapper {

	boolean nowrap() default false;

}
