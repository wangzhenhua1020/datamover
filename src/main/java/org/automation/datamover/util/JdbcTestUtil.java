package org.automation.datamover.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTestUtil {

	private static Logger logger = LoggerFactory.getLogger(JdbcTestUtil.class);

	public static void test(String driver, String url, String username, String password)
			throws SQLException, ClassNotFoundException {
		Class.forName(driver);
		Connection connection = DriverManager.getConnection(url, username, password);
		close(connection);
	}

	private static void close(Connection connection){
		try {
			if(connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			logger.warn(e.getMessage());
		}
	}

}
