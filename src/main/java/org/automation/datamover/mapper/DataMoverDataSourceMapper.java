package org.automation.datamover.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.automation.datamover.bean.db.DataMoverDataSource;
import org.springframework.stereotype.Repository;

@Repository
public interface DataMoverDataSourceMapper {

	@Select("select * from data_mover_datasource where id = #{id}")
	DataMoverDataSource get(Integer id);

	@Select("<script>select * from data_mover_datasource" +
			"<where>" +
			"<if test='status != null'>AND status = #{status}</if>" +
			"</where>" +
			"order by create_time desc" +
			"</script>")
	List<DataMoverDataSource> list(Integer status);

	@Insert("insert into data_mover_datasource " +
			"(name, db_type, jdbc_url, jdbc_driver, jdbc_username, jdbc_password, test_sql, status, create_time, update_time, remark)" +
			" values (#{name}, #{dbType}, #{jdbcUrl}, #{jdbcDriver}, #{jdbcUsername}, #{jdbcPassword}, " +
			"#{testSql, jdbcType = VARCHAR}, #{status}, #{createTime}, #{updateTime}, #{remark, jdbcType = VARCHAR})")
	@Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
	void add(DataMoverDataSource bean);

	@Update("update data_mover_datasource set name = #{name}, db_type = #{dbType}, jdbc_url = #{jdbcUrl}, jdbc_driver = #{jdbcDriver}, " +
			"jdbc_username = #{jdbcUsername}, jdbc_password = #{jdbcPassword}, test_sql = #{testSql, jdbcType = VARCHAR}, " +
			"status = #{status}, update_time = #{updateTime}, remark = #{remark, jdbcType = VARCHAR} where id = #{id}")
	void update(DataMoverDataSource bean);

	@Delete("delete from data_mover_datasource where id = #{id}")
	void delete(Integer id);

}
