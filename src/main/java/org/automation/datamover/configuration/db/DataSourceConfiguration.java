package org.automation.datamover.configuration.db;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.automation.datamover.util.MapWrapperFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;

@Configuration
@MapperScan(basePackages = "org.automation.datamover.mapper")
public class DataSourceConfiguration {

	@Primary
	@Bean
	@ConfigurationProperties("spring.datasource.primary")
	public DataSourceProperties dataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@ConfigurationProperties("spring.datasource.common")
	public CommonDataSourceProperties commonDataSourceProperties() {
		return new CommonDataSourceProperties();
	}

	@Primary
	@Bean(name = "primary.datasource", destroyMethod = "close", initMethod = "init")
	@ConfigurationProperties(prefix = "spring.datasource.primary")
	public DataSource druidDataSource() {
		return new DruidDataSource();
	}

	@Bean
	@ConfigurationProperties(prefix = "mybatis.configuration")
	public org.apache.ibatis.session.Configuration mybatisConfiguration() {
		return new org.apache.ibatis.session.Configuration();
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(@Qualifier("dynamicDataSource") DataSource dataSource,
			org.apache.ibatis.session.Configuration configuration) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setObjectWrapperFactory(new MapWrapperFactory());//Map中的KEY全部转换为大小
		bean.setDataSource(dataSource);
		bean.setConfiguration(configuration);
		return bean.getObject();
	}

	@Bean
	public DataSourceTransactionManager transactionManager(@Qualifier("dynamicDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean
	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Bean
	public DynamicDataSource dynamicDataSource(@Qualifier("primary.datasource") DataSource defaultDS) {
		DynamicDataSource dynamicDataSource = DynamicDataSource.getInstance();
		dynamicDataSource.setTargetDataSources(new HashMap<>());
		dynamicDataSource.setDefaultTargetDataSource(defaultDS);
		return dynamicDataSource;
	}

}
