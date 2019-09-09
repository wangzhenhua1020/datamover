package org.automation.datamover.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.automation.datamover.bean.db.DataMoverTemplateGroup;
import org.springframework.stereotype.Repository;

@Repository
public interface DataMoverTemplateGroupMapper {

	@Select("select * from data_mover_template_group order by order_index asc, name asc")
	List<DataMoverTemplateGroup> list();

	@Select("select * from data_mover_template_group where id = #{id}")
	DataMoverTemplateGroup get(Integer id);

	@Select("select * from data_mover_template_group where parent_id = #{parentId}")
	List<DataMoverTemplateGroup> listChildren(Integer parentId);

	@Insert("insert into data_mover_template_group (name, parent_id, path, create_time, update_time, remark) values (" +
			"#{name, jdbcType = VARCHAR}, #{parentId, jdbcType = NUMERIC}, #{path, jdbcType = VARCHAR}," +
			"#{createTime, jdbcType = TIMESTAMP}, #{updateTime, jdbcType = TIMESTAMP}, #{remark, jdbcType = VARCHAR})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void add(DataMoverTemplateGroup group);

	@Update("<script>update data_mover_template_group <set> " +
			"<if test='name != null'>name = #{name, jdbcType = VARCHAR},</if> " +
			"<if test='parentId != null'>parent_id = #{parentId, jdbcType = NUMERIC},</if> " +
			"<if test='path != null'>path = #{path, jdbcType = VARCHAR},</if> " +
			"<if test='updateTime != null'>update_time = #{updateTime, jdbcType = TIMESTAMP},</if> " +
			"<if test='remark != null'>remark = #{remark, jdbcType = VARCHAR},</if> " +
			"</set> " +
			"where id = #{id}</script>")
	void update(DataMoverTemplateGroup group);

	@Delete("delete from data_mover_template_group where id = #{id}")
	void delete(Integer id);

}
