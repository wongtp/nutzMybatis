package com.wong.mutzMybatis.mapping;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Options.FlushCachePolicy;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.wong.mutzMybatis.dto.DataRecord;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年7月14日 上午10:57:39
 * 封装的少量通用增删改查的操作
 */
public interface BaseMapper {
	
	@Select("select * from ${tableName} where 1 = 1 and ${primaryKeyName} = #{primaryKeyValue}")
	@Options(useCache = true, flushCache = FlushCachePolicy.FALSE, timeout = 5000)
	@ResultType(DataRecord.class)
	DataRecord getDataRecord(String tableName, String primaryKeyName, Object primaryKeyValue) throws Exception;
	
	//@SelectProvider(type = BaseMapperSqlProvider.class, method="getTableListSql")
	@Select({"<script>",
			"SELECT * FROM ${tableName} t",
			"	where 1 = 1",
			"	<if test='parameters != null'>",
			"		<foreach collection='parameters' index='key'  item='value' separator=' ' >",
			"	        and ${key} = #{value}",
			"		</foreach>",
			"	</if>",
			"</script>"})
	@Options(useCache = true, flushCache = FlushCachePolicy.FALSE, timeout = 5000)
	@ResultType(DataRecord.class)
	List<DataRecord> getTableList(String tableName, Object parameters) throws Exception;
	
	@Select({"<script>",
			"SELECT count(0) FROM ${tableName} t where 1 = 1",
			"	<if test='parameters != null'>",
			"		<foreach collection='parameters' index='key'  item='value' separator=' ' >",
			"	       and ${key} = #{value}",
			"		</foreach>",
			"	</if>",
			"</script>"})
	Long getCount(String tableName, Object parameters) throws Exception;
	
	@Delete("delete from ${tableName} where 1 = 1 and ${primaryKey} = #{primaryKeyValue}")
	Integer delete(String tableName, String primaryKey, Object primaryKeyValue) throws Exception;
	
	@Delete("delete from ${tableName} where 1 = 1 and ${primaryKey} in (${primaryKeyValues})")
	Integer deleteByIds(String tableName, String primaryKey, String primaryKeyValues) throws Exception;
	
	@Delete({
		"<script>",
		"<if test=\"parameters != null and tableName!=null and tableName != '' \">" + 
		"	delete from ${tableName} WHERE 1 = 1" + 
		"	<if test='parameters != null'>",
		"		<foreach collection='parameters' index='key'  item='value' separator=' ' >",
		"			and ${key} = #{value}",
		"		</foreach>",
		"	</if>",
		"</if>",
		"</script>"})
	Integer deleteByParam(String tableName, Object parameters) throws Exception;
	
	@Update("<script>update ${tableName} SET ${values} <if test=\"where != null and where != ''\"> where ${where} </if></script>")
	Integer update(Object parameters) throws Exception;
	
	@Insert("insert into ${tableName}(${columns}) values(${values})")
	Integer insert(Object parameters) throws Exception;
	
	@Insert({
		"<script>",
		"insert into ${tableName}(${columns})" + 
		"	<if test=\"list != null\">" + 
		"		<foreach collection=\"list\" item=\"item\" separator=\" union \">" + 
		"			select ${items} from dual" + 
		"		</foreach>" + 
		"	</if>",
		"</script>"})
	Integer insertList(Object parameters) throws Exception;
	
}
