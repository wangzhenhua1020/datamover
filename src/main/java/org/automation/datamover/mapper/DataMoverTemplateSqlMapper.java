package org.automation.datamover.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.automation.datamover.bean.db.DataMoverTemplateSql;
import org.automation.datamover.bean.req.DataMoverTemplateGroupListQO;
import org.automation.datamover.bean.resp.DataMoverTemplateSqlVO;
import org.springframework.stereotype.Repository;

@Repository
public interface DataMoverTemplateSqlMapper {

	@Select("<script>select s.*, g.name as groupName from data_mover_template_sql s " +
			"left outer join data_mover_template_group g on s.group_id = g.id " +
			"<where>" +
			"<if test='name != null and !name.isEmpty()'>AND s.name like #{name}</if>" +
			"<if test='groupPath != null'>AND g.path like #{groupPath}</if>" +
			"<if test='groupId != null'>AND s.group_id = #{groupId}</if>" +
			"</where>" +
			"</script>")
	List<DataMoverTemplateSqlVO> list(DataMoverTemplateGroupListQO qo);

	@Insert("insert into data_mover_template_sql (name, group_id, type, content, create_time, update_time, remark) values (" +
			"#{name, jdbcType = VARCHAR}, #{groupId, jdbcType = NUMERIC}, #{type, jdbcType = NUMERIC}, #{content, jdbcType = VARCHAR}," +
			"#{createTime, jdbcType = TIMESTAMP}, #{updateTime, jdbcType = TIMESTAMP}, #{remark, jdbcType = VARCHAR})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void add(DataMoverTemplateSql template);

	@Update("<script>update data_mover_template_sql <set> " +
			"<if test='name != null'>name = #{name, jdbcType = VARCHAR},</if> " +
			"<if test='groupId != null'>group_id = #{groupId, jdbcType = NUMERIC},</if> " +
			"<if test='type != null'>type = #{type, jdbcType = NUMERIC},</if> " +
			"<if test='content != null'>content = #{content, jdbcType = VARCHAR},</if> " +
			"<if test='updateTime != null'>update_time = #{updateTime, jdbcType = TIMESTAMP},</if> " +
			"<if test='remark != null'>remark = #{remark, jdbcType = VARCHAR},</if> " +
			"</set> " +
			"where id = #{id}</script>")
	void update(DataMoverTemplateSql template);

	@Delete("delete from data_mover_template_sql where id = #{id}")
	void delete(Integer id);

}
