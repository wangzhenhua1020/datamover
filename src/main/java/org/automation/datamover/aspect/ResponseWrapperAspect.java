package org.automation.datamover.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.automation.datamover.bean.ResponseWrapper;
import org.automation.datamover.bean.resp.DataResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 加工Controller层返回值
 */
@Aspect
@Order(0)
@Component
public class ResponseWrapperAspect {

	private static Logger logger = LoggerFactory.getLogger(ResponseWrapperAspect.class);

	@Around("@annotation(wrapper)")
	public Object processResponse(ProceedingJoinPoint joinpoint, ResponseWrapper wrapper) throws Throwable {
		Object result = null;
		try {
			Object[] args = joinpoint.getArgs();
			result = joinpoint.proceed(args);
			if (wrapper.nowrap()) {
				return result;
			} else {
				return new DataResponse<Object>(true, null, result);
			}
		} catch(Throwable t) {
			logger.error(t.getMessage(), t);
			if (wrapper.nowrap()) {
				throw t;
			} else {
				return new DataResponse<Object>(false, t.getMessage(), null);
			}
		}
	}

}
