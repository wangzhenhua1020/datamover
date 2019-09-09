package org.automation.datamover.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.automation.datamover.bean.Constant;
import org.automation.datamover.bean.ext.DataMoverConfigDetail;
import org.automation.datamover.mapper.mover.DestDataMapper;
import org.automation.datamover.mapper.mover.SrcDataMapper;
import org.automation.datamover.service.DataMoverService;
import org.automation.datamover.service.worker.DataMoveBroadcaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;

@Service
public class DataMoverServiceImpl implements DataMoverService {

	@Autowired
	private SrcDataMapper srcDataMapper;

	@Autowired
	private DestDataMapper destDataMapper;

	@Value("${datamover.dest.group-count.delete}")
	private Integer deleteGroupCount;

	@Value("${datamover.dest.group-count.insert}")
	private Integer insertGroupCount;

	private Integer getDeleteGroupCount() {
		//Oracle IN语句上线999
		return deleteGroupCount == null || deleteGroupCount <= 0 ? 999 : deleteGroupCount;
	}

	private Integer getInsertGroupCount() {
		return insertGroupCount == null || insertGroupCount <= 0 ? 500 : insertGroupCount;
	}

	@Transactional
	public String srcUpdate(String sql, DataMoveBroadcaster broadcaster) {
		Integer count = srcDataMapper.update(sql);
		return "更新" + count + "条数据";
	}

	@Transactional
	public List<Map<String, Object>> srcList(String sql) {
		return srcDataMapper.list(sql);
	}

	/**
	 * 根据主键删除目标库中对应的记录，然后再进行insert
	 */
	@Transactional
	public String destUpdate(List<Map<String, Object>> list, DataMoverConfigDetail config, DataMoveBroadcaster broadcaster) {
		if (list == null || list.isEmpty()) {
			return "源数据条数为空";
		}
		List<String> primaryKeys = getPrimaryKeyList(config.getPrimaryKeyListJson());
		int deleteCount = 0;
		int insertCount = 0;
		if (config.getDestTableDeleteType() == Constant.DEST_TABLE_DELETE_TYPE_ALL) {//全部删除
			deleteCount = destDataMapper.deleteAll(config.getDestTable());
		} else if (!primaryKeys.isEmpty()) {//删除目标表对应记录
			//批量删除方式（为防止一个SQL过长，进行拆分执行）
			List<List<Map<String, Object>>> batchList = groupList(list, getDeleteGroupCount());
			for (List<Map<String, Object>> rows: batchList) {
				if (broadcaster.isTimeout()) {
					throw new IllegalStateException("任务超时");
				}
				if (broadcaster.isStoped()) {
					throw new IllegalStateException("手动停止");
				}
				if (config.getDestTableDeleteType() == Constant.DEST_TABLE_DELETE_TYPE_IN) {
					deleteCount += destDataMapper.deleteByPkWithOrMethod(config.getDestTable(), primaryKeys, rows);
				} else {
					deleteCount += destDataMapper.deleteByPkWithInMethod(config.getDestTable(), primaryKeys, rows);
				}
			}
			//单条删除方式
			//for (Map<String, Object> row: list) {
			//	destDataMapper.deleteByPk(config.getDestTable(), primaryKeys, row);
			//}
		}
		//统计所有列
		Set<String> columnSet = new HashSet<>();
		for (Map<String, Object> row: list) {
			for (Map.Entry<String, Object> entry: row.entrySet()) {
				if (!columnSet.contains(entry.getKey())) {
					columnSet.add(entry.getKey());
				}
			}
		}
		List<String> columns = new ArrayList<>();
		for(String column: columnSet.toArray(new String[] {})) {
			columns.add(column);
		}
		//插入新数据
		//destDataMapper.insertBatch(config.getDestTable(), columns, list);
		if ("mppdb".equalsIgnoreCase(config.getDestDs().getDbType())
				|| "postgres".equalsIgnoreCase(config.getDestDs().getDbType())
				|| "postgresql".equalsIgnoreCase(config.getDestDs().getDbType())
				|| "mysql".equalsIgnoreCase(config.getDestDs().getDbType())) {
			List<List<Map<String, Object>>> batchList = groupList(list, getInsertGroupCount());
			for (List<Map<String, Object>> rows: batchList) {
				if (broadcaster.isTimeout()) {
					throw new IllegalStateException("任务超时");
				}
				insertCount += destDataMapper.insertBatch(config.getDestTable(), columns, rows);
			}
		} else {
			for (Map<String, Object> row: list) {
				if (broadcaster.isTimeout()) {
					throw new IllegalStateException("任务超时");
				}
				insertCount += destDataMapper.insert(config.getDestTable(), columns, row);
			}
		}
		return "删除" + deleteCount + "条数据，新增" + insertCount + "条数据";
	}

	private List<List<Map<String, Object>>> groupList(List<Map<String, Object>> list, int groupCount) {
		List<List<Map<String, Object>>> result = new ArrayList<>();
		if (list != null & list.size() > 0 && groupCount > 0) {
			for (int i = 0; i < Math.ceil(Integer.valueOf(list.size()).doubleValue() / groupCount); i++) {
				List<Map<String, Object>> subRows = new ArrayList<>();
				for (int j = 0; j < groupCount; j++) {
					if (j + i * groupCount >= list.size()) {
						break;
					}
					subRows.add(list.get(j + i * groupCount));
				}
				result.add(subRows);
			}
		}
		return result;
	}

	private List<String> getPrimaryKeyList(String keyArrJson) {//获取主键集合
		List<String> primaryKeys = new ArrayList<>();
		if (keyArrJson == null || keyArrJson.trim().isEmpty()) {
			return primaryKeys;
		}
		JSONArray pkArr = JSONArray.parseArray(keyArrJson.toUpperCase());//Mybatis查询出的Map中的key为大写
		for (int i = 0; i < pkArr.size(); i++) {
			Object tmp = pkArr.get(i);
			if (tmp == null) {
				continue;
			}
			String key = String.valueOf(tmp).trim();
			if (key.isEmpty()) {
				continue;
			}
			primaryKeys.add(key);
		}
		return primaryKeys;
	}

}
