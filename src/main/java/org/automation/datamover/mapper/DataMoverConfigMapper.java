package org.automation.datamover.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.automation.datamover.bean.db.DataMoverConfig;
import org.automation.datamover.bean.req.DataMoverConfigListQO;
import org.automation.datamover.bean.resp.DataMoverConfigVO;
import org.springframework.stereotype.Repository;

@Repository
public interface DataMoverConfigMapper {

	@Select("<script>select c.*, s.name as srcDsName, d.name as destDsName from data_mover_config c " + 
			"left outer join data_mover_datasource s on c.src_ds_id = s.id " + 
			"left outer join data_mover_datasource d on c.dest_ds_id = d.id " + 
			"<where>" +
			"<if test='name != null and !name.isEmpty()'>AND c.name like #{name}</if>" +
			"<if test='srcDsName != null and !srcDsName.isEmpty()'>AND s.name like #{srcDsName}</if>" +
			"<if test='destDsName != null and !destDsName.isEmpty()'>AND d.name like #{destDsName}</if>" +
			"<if test='status != null'>AND c.status = #{status}</if>" +
			"</where>" +
			"order by create_time desc</script>")
	List<DataMoverConfigVO> list4vo(DataMoverConfigListQO qo);

	@Select("select * from data_mover_config where id = #{id}")
	DataMoverConfig get(Integer id);

	@Insert("insert into data_mover_config (name, src_ds_id, src_sql_type, src_sql, dest_ds_id, dest_table, " +
			"dest_table_delete_type, primary_key_list_json, timeout, singleton, status, " +
			"post_action, post_condition, post_data, create_time, update_time, remark) values (" +
			"#{name, jdbcType = VARCHAR}, #{srcDsId, jdbcType = NUMERIC}, #{srcSqlType, jdbcType = NUMERIC}," +
			"#{srcSql, jdbcType = VARCHAR}, #{destDsId, jdbcType = NUMERIC}, #{destTable, jdbcType = VARCHAR}, " +
			"#{destTableDeleteType, jdbcType = NUMERIC}, #{primaryKeyListJson, jdbcType = VARCHAR}, " +
			"#{timeout, jdbcType = NUMERIC}, #{singleton, jdbcType = NUMERIC}, #{status, jdbcType = NUMERIC}, " +
			"#{postAction, jdbcType = NUMERIC}, #{postCondition, jdbcType = NUMERIC}, #{postData, jdbcType = VARCHAR}, " +
			"#{createTime, jdbcType = TIMESTAMP}, #{updateTime, jdbcType = TIMESTAMP}, #{remark, jdbcType = VARCHAR})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void add(DataMoverConfig config);

	@Update("<script>update data_mover_config <set> " +
			"<if test='name != null'>name = #{name, jdbcType = VARCHAR},</if> " +
			"<if test='srcDsId != null'>src_ds_id = #{srcDsId, jdbcType = NUMERIC},</if> " +
			"<if test='srcSqlType != null'>src_sql_type = #{srcSqlType, jdbcType = NUMERIC},</if> " +
			"<if test='srcSql != null'>src_sql = #{srcSql, jdbcType = VARCHAR},</if> " +
			"<if test='destDsId != null'>dest_ds_id = #{destDsId, jdbcType = NUMERIC},</if> " +
			"<if test='destTable != null'>dest_table = #{destTable, jdbcType = VARCHAR},</if> " +
			"<if test='destTableDeleteType != null'>dest_table_delete_type = #{destTableDeleteType, jdbcType = NUMERIC},</if> " +
			"<if test='primaryKeyListJson != null'>primary_key_list_json = #{primaryKeyListJson, jdbcType = VARCHAR},</if> " +
			"<if test='timeout != null'>timeout = #{timeout, jdbcType = NUMERIC},</if> " +
			"<if test='singleton != null'>singleton = #{singleton, jdbcType = NUMERIC},</if> " +
			"<if test='status != null'>status = #{status, jdbcType = NUMERIC},</if> " +
			"<if test='postAction != null'>post_action = #{postAction, jdbcType = NUMERIC},</if> " +
			"<if test='postCondition != null'>post_condition = #{postCondition, jdbcType = NUMERIC},</if> " +
			"<if test='postData != null'>post_data = #{postData, jdbcType = VARCHAR},</if> " +
			"<if test='updateTime != null'>update_time = #{updateTime, jdbcType = TIMESTAMP},</if> " +
			"<if test='remark != null'>remark = #{remark, jdbcType = VARCHAR},</if> " +
			"</set> " +
			"where id = #{id}</script>")
	void update(DataMoverConfig config);

	@Delete("delete from data_mover_config where id = #{id}")
	void delete(Integer id);

}
