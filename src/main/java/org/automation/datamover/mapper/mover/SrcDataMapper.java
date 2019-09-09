package org.automation.datamover.mapper.mover;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface SrcDataMapper {

	@Update("${sql}")
	Integer update(String sql);

	@Select("${sql}")
	List<Map<String, Object>> list(String sql);

}
