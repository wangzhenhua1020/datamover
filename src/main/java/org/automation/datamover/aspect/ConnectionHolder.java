package org.automation.datamover.aspect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.automation.datamover.bean.ConnectionHelper;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 获取Connection层返回值
 */
@Aspect
@Order(1)
@Component
public class ConnectionHolder {

	private static Logger logger = LoggerFactory.getLogger(ConnectionHolder.class);

	private static Map<Thread, Connection> conns = Collections.synchronizedMap(new HashMap<>());

	@Autowired
	private SqlSessionTemplate sqlSessionTemplate;

	@Around("@annotation(wrapper)")
	public Object cacheConnection(ProceedingJoinPoint joinpoint, ConnectionHelper wrapper) throws Throwable {
		conns.put(Thread.currentThread(), sqlSessionTemplate.getConnection());
		try {
			Object[] args = joinpoint.getArgs();
			return joinpoint.proceed(args);
		} finally {
			conns.remove(Thread.currentThread());
		}
	}

	public static void kill(Thread thread) {
		Connection conn = conns.get(thread);
		if (conn == null) {
			return;
		}
		synchronized (conn) {
			try {
				conn.rollback();
				conn.close();
			} catch (SQLException e) {
				logger.warn("强制关闭连接出错", e);
			}
		}
		
	}

}
