package org.automation.datamover.mapper.mover;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface DestDataMapper {

	@Insert("<script>" +
			"INSERT INTO ${tableName} (" + 
			"	<foreach collection='columns' item='column' separator=','>" + 
			"		${column}" + 
			"	</foreach>" + 
			") VALUES (" + 
			"	<foreach collection='columns' item='column'  separator=','>" + 
			"		#{row.${column}}" + 
			"	</foreach>" + 
			")" + 
			"</script>")
	int insert(@Param("tableName") String tableName, @Param("columns") List<String> columns, @Param("row") Map<String, Object> row);

	@Insert("<script>" +
			"INSERT INTO ${tableName} (" + 
			"	<foreach collection='columns' item='column' separator=','>" + 
			"		${column}" + 
			"	</foreach>" + 
			") VALUES " + 
			"	<foreach collection='rows' item='row' separator=','>" + 
			"		(<foreach collection='columns' item='column'  separator=','>" + 
			"			#{row.${column}}" + 
			"		</foreach>)" + 
			"	</foreach>" + 
			"</script>")
	int insertBatch(@Param("tableName") String tableName, @Param("columns") List<String> columns, @Param("rows") List<Map<String, Object>> rows);

	@Delete("<script>" +
			"DELETE FROM ${tableName} WHERE" + 
			"	(<foreach collection='columns' item='column' index='index' open='(' close=')' separator=','>" + 
			"		${column}" + 
			"	</foreach>) IN (" + 
			"	<foreach collection='rows' item='row' separator=','>" + 
			"		<foreach collection='columns' item='column' index='index' open='(' close=')' separator=','>" + 
			"			#{row.${column}}" + 
			"		</foreach>" + 
			"	</foreach>)" + 
			"</script>")
	int deleteByPkWithInMethod(@Param("tableName") String tableName, @Param("columns") List<String> columns, @Param("rows") List<Map<String, Object>> rows);

	@Delete("<script>" +
			"DELETE FROM ${tableName} WHERE" + 
			"	<foreach collection='rows' item='row' separator=' OR '>" + 
			"		<foreach collection='columns' item='column' index='index' open='(' close=')' separator=' AND '>" + 
			"			<choose>" + 
			"				<when test=\"row.get(column) == null\">" + 
			"					${column} is NULL" + 
			"				</when>" + 
			"				<otherwise>" + 
			"					${column} = #{row.${column}}" + 
			"				</otherwise>" + 
			"			</choose>" + 
			"		</foreach>" + 
			"	</foreach>" + 
			"</script>")
	int deleteByPkWithOrMethod(@Param("tableName") String tableName, @Param("columns") List<String> columns, @Param("rows") List<Map<String, Object>> rows);

	@Delete("<script>" +
			"DELETE FROM ${tableName} WHERE" + 
			"<foreach collection='columns' item='column' index='index' separator=' AND '>" + 
			"	<choose>" + 
			"		<when test=\"row.get(column) == null\">" + 
			"			${column} is NULL" + 
			"		</when>" + 
			"		<otherwise>" + 
			"			${column} = #{row.${column}}" + 
			"		</otherwise>" + 
			"	</choose>" + 
			"</foreach>" + 
			"</script>")
	int deleteByPk(@Param("tableName") String tableName, @Param("columns") List<String> columns, @Param("row") Map<String, Object> row);

	@Update("DELETE FROM ${tableName}")
	int deleteAll(@Param("tableName") String tableName);

}
