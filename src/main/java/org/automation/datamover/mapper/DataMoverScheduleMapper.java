package org.automation.datamover.mapper;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.automation.datamover.bean.db.DataMoverSchedule;
import org.automation.datamover.bean.resp.DataMoverScheduleVO;
import org.springframework.stereotype.Repository;

@Repository
public interface DataMoverScheduleMapper {

	@Select("select * from data_mover_schedule where id = #{id}")
	DataMoverSchedule get(Integer id);

	@Select("<script>select * from data_mover_schedule" +
			"<where>" +
			"<if test='status != null'>AND status = #{status}</if>" +
			"<if test='configId != null'>AND config_id = #{configId}</if>" +
			"</where>" +
			"</script>")
	List<DataMoverSchedule> list(@Param("status") Integer status, @Param("configId") Integer configId);

	@Select("select t.*, t1.name as configName from data_mover_schedule t left outer join data_mover_config t1 on t.config_id = t1.id order by t.create_time desc")
	List<DataMoverScheduleVO> list4vo();

	@Insert("insert into data_mover_schedule " +
			"(name, config_id, expr, status, create_time, update_time, remark)" +
			" values (#{name}, #{configId}, #{expr}, #{status}, #{createTime}, #{updateTime}, #{remark, jdbcType = VARCHAR})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void add(DataMoverSchedule bean);

	@Update("update data_mover_schedule set name = #{name}, config_id = #{configId}, expr = #{expr}, " +
			"status = #{status}, update_time = #{updateTime}, remark = #{remark, jdbcType = VARCHAR} where id = #{id}")
	void update(DataMoverSchedule bean);

	@Delete("delete from data_mover_schedule where id = #{id}")
	void delete(Integer id);

	@Update("update data_mover_schedule set status = #{status}, update_time = #{updateTime} where id = #{id}")
	void changeStatus(@Param("id") Integer id, @Param("status") Integer status, @Param("updateTime") Timestamp updateTime);

	@Update("update data_mover_schedule set status = #{status}, update_time = #{updateTime} where config_id = #{configId}")
	void changeStatusByConfig(@Param("configId") Integer configId, @Param("status") Integer status, @Param("updateTime") Timestamp updateTime);

}
