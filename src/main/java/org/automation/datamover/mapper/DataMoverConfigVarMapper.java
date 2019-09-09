package org.automation.datamover.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.automation.datamover.bean.db.DataMoverConfigVar;
import org.springframework.stereotype.Repository;

@Repository
public interface DataMoverConfigVarMapper {

	@Select("select * from data_mover_config_var where config_id = #{configId}")
	List<DataMoverConfigVar> list(Integer configId);

	@Insert("insert into data_mover_config_var (config_id, name, type, value, update_time, remark) values (" +
			"#{configId, jdbcType = NUMERIC}, #{name, jdbcType = VARCHAR}, #{type, jdbcType = NUMERIC}," +
			"#{value, jdbcType = VARCHAR}, #{updateTime, jdbcType = TIMESTAMP}, #{remark, jdbcType = VARCHAR})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void add(DataMoverConfigVar var);

	@Update("<script>update data_mover_config_var <set> " +
			"<if test='name != null'>name = #{name, jdbcType = VARCHAR},</if> " +
			"<if test='type != null'>type = #{type, jdbcType = NUMERIC},</if> " +
			"<if test='value != null'>value = #{value, jdbcType = VARCHAR},</if> " +
			"<if test='updateTime != null'>update_time = #{updateTime, jdbcType = TIMESTAMP},</if> " +
			"<if test='remark != null'>remark = #{remark, jdbcType = VARCHAR},</if> " +
			"</set> " +
			"where id = #{id}</script>")
	void update(DataMoverConfigVar var);

	@Delete("delete from data_mover_config_var where id = #{id}")
	void delete(Integer id);

	@Delete("delete from data_mover_config_var where config_id = #{configId}")
	void deleteByConfig(Integer configId);

}
