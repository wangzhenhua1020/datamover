package org.automation.datamover.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.automation.datamover.bean.Constant;
import org.automation.datamover.bean.db.DataMoverDataSource;
import org.automation.datamover.bean.req.DataMoverDataSourceListQO;
import org.automation.datamover.configuration.db.CommonDataSourceProperties;
import org.automation.datamover.configuration.db.DynamicDataSource;
import org.automation.datamover.mapper.DataMoverDataSourceMapper;
import org.automation.datamover.service.DataMoverDataSourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class DataMoverDataSourceServiceImpl implements DataMoverDataSourceService {

	private static Logger logger = LoggerFactory.getLogger(DataMoverDataSourceServiceImpl.class);

	@Autowired
	private DataMoverDataSourceMapper dataMoverDataSourceMapper;

	@Autowired
	private CommonDataSourceProperties commonDataSourceProperties;

	@Override
	public PageInfo<DataMoverDataSource> listByPage(DataMoverDataSourceListQO qo) {
		PageHelper.startPage(qo.getPageNo(), qo.getLimit());
		return new PageInfo<DataMoverDataSource>(dataMoverDataSourceMapper.list(qo.getStatus()));
	}

	@Override
	public List<DataMoverDataSource> list(DataMoverDataSourceListQO qo) {
		return dataMoverDataSourceMapper.list(qo.getStatus());
	}

	@Override
	public DataMoverDataSource get(Integer id) {
		return dataMoverDataSourceMapper.get(id);
	}

	@Override
	@PostConstruct
	public synchronized void reload() {
		List<DataMoverDataSource> list = dataMoverDataSourceMapper.list(Constant.STATUS_AVAILABLE);
		Map<Object, Object> dsMap = new HashMap<>();
		for (DataMoverDataSource info: list) {
			DruidDataSource ds = new DruidDataSource();
			ds.setUrl(info.getJdbcUrl());
			ds.setDriverClassName(info.getJdbcDriver());
			ds.setUsername(info.getJdbcUsername());
			ds.setPassword(info.getJdbcPassword());
			//数据源属性设置
			ds.setMaxWait(commonDataSourceProperties.getMaxWait());
			ds.setTimeBetweenEvictionRunsMillis(commonDataSourceProperties.getTimeBetweenEvictionRunsMillis());
			ds.setMinEvictableIdleTimeMillis(commonDataSourceProperties.getMinEvictableIdleTimeMillis());
			if (info.getTestSql() != null && !info.getTestSql().isEmpty()) {
				ds.setValidationQuery(info.getTestSql());
			} else {
				ds.setValidationQuery(commonDataSourceProperties.getValidationQuery());
			}
			ds.setTestWhileIdle(commonDataSourceProperties.isTestWhileIdle());
			ds.setTestOnBorrow(commonDataSourceProperties.isTestOnBorrow());
			ds.setTestOnReturn(commonDataSourceProperties.isTestOnReturn());
			ds.setBreakAfterAcquireFailure(commonDataSourceProperties.isBreakAfterAcquireFailure());
			dsMap.put(info.getId(), ds);
		}
		DynamicDataSource.getInstance().setTargetDataSources(dsMap);
	}

	@Override
	@Transactional
	public void add(DataMoverDataSource bean) {
		Timestamp now = new Timestamp(System.currentTimeMillis());
		bean.setCreateTime(now);
		bean.setUpdateTime(now);
		bean.setTestSql(getTestSql(bean.getDbType()));
		dataMoverDataSourceMapper.add(bean);
		reloadSilent("增加数据源后重新加载异常");
	}

	@Override
	@Transactional
	public void update(DataMoverDataSource bean) {
		bean.setUpdateTime(new Timestamp(System.currentTimeMillis()));
		bean.setTestSql(getTestSql(bean.getDbType()));
		dataMoverDataSourceMapper.update(bean);
		reloadSilent("修改数据源后重新加载异常");
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		dataMoverDataSourceMapper.delete(id);
		reloadSilent("删除数据源后重新加载异常");
	}

	private void reloadSilent(String errorMessage) {
		try {
			reload();
		} catch(Exception e) {
			logger.warn((errorMessage != null ? errorMessage + ": " : "") + e.getMessage(), e);
		}
	}

	private String getTestSql(String dbType) {
		String sql1 = "select 1";
		String sql2 = "select 1 from dual";
		if (dbType == null) {
			return sql1;
		}
		if ("oracle".equalsIgnoreCase(dbType)) {
			return sql2;
		} else {
			return sql1;
		}
	}

}
