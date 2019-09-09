package org.automation.datamover.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.automation.datamover.bean.db.DataMoverInst;
import org.automation.datamover.bean.req.DataMoverInstListQO;
import org.automation.datamover.bean.resp.DataMoverInstVO;
import org.springframework.stereotype.Repository;

@Repository
public interface DataMoverInstMapper {

	@Select("<script>select t.*, t1.name from data_mover_inst t left outer join data_mover_config t1 on t.config_id = t1.id " +
			"<where>" +
			"<if test='configName != null and !configName.isEmpty()'> AND t1.name like #{configName}</if>" +
			"<if test='triggerType != null'> AND t.trigger_type = #{triggerType}</if>" +
			"<if test='status != null'> AND t.status = #{status}</if>" +
			"<if test='startTime != null'> AND t.create_time &gt;= #{startTime}</if>" +
			"<if test='endTime != null'> AND t.create_time &lt;= #{endTime}</if>" +
			"<if test='configId != null'> AND t.config_id = #{configId}</if>" +
			"</where>" +
			"order by t.create_time desc, t.id desc</script>")
	List<DataMoverInstVO> list4vo(DataMoverInstListQO qo);

	@Select("<script>select * from data_mover_inst<where>" +
			"<if test='configId != null'> AND config_id = #{configId}</if>" +
			"<if test='statusList != null'>" +
				"<foreach collection='statusList' item='status' open=' AND status in (' separator=',' close=')'>#{status}</foreach>" +
			"</if>" +
			"<if test='excludeIdList != null'>" +
				"<foreach collection='excludeIdList' item='id' open=' AND id not in (' separator=',' close=')'>#{id}</foreach>" +
			"</if>" +
			"<if test='earliestTime != null'> AND create_time &gt;= #{earliestTime}</if>" +
			"</where></script>")
	List<DataMoverInst> listUnfinished(@Param("configId") Integer configId,
			@Param("excludeIdList") List<Integer> excludeIdList,
			@Param("statusList") List<Integer> unfinishedStatusList,
			@Param("earliestTime")Timestamp earliestTime);

	@Select("select * from data_mover_inst where id = #{id}")
	DataMoverInst get(Integer id);

	@Insert("insert into data_mover_inst (config_id, trigger_type, status, message, data, start_time, end_time, create_time, update_time) values (" +
			"#{configId, jdbcType = NUMERIC}, #{triggerType, jdbcType = NUMERIC}, #{status, jdbcType = NUMERIC}," +
			"#{message, jdbcType = VARCHAR}, #{data, jdbcType = VARCHAR}, #{startTime, jdbcType = TIMESTAMP}," +
			"#{endTime, jdbcType = TIMESTAMP}, #{createTime, jdbcType = TIMESTAMP}, #{updateTime, jdbcType = TIMESTAMP})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void add(DataMoverInst inst);

	@Update("<script>update data_mover_inst <set> " +
			"<if test='status != null'>status = #{status, jdbcType = NUMERIC},</if> " +
			"<if test='message != null'>message = #{message, jdbcType = VARCHAR},</if> " +
			"<if test='data != null'>data = #{data, jdbcType = VARCHAR},</if> " +
			"<if test='startTime != null'>start_time = #{startTime, jdbcType = TIMESTAMP},</if> " +
			"<if test='endTime != null'>end_time = #{endTime, jdbcType = TIMESTAMP},</if> " +
			"<if test='updateTime != null'>update_time = #{updateTime, jdbcType = TIMESTAMP},</if> " +
			"</set> " +
			"where id = #{id}</script>")
	void update(DataMoverInst inst);

}
