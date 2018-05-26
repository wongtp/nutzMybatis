package cn.wizzer.app.wb.modules.common.nutzMybatis.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;

import cn.wizzer.app.wb.modules.common.nutzMybatis.dto.DataRecord;
import cn.wizzer.app.wb.modules.common.nutzMybatis.exception.DaoException;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年3月27日 下午2:22:10
 * @describe 配合  base-mapper.xml 封装了少量 CRUD 的操作，复杂点的的还是建议自己写吧~我是不会帮你写的
 */
public interface BaseDao {
	
	/**
	 * 返回 Integer 型数据。
	 * @param statement ： mapper语句id
	 * @param parameters ：参数
	 */
	Integer getInt(String statement, Object parameters);
	
	/**
	 * 返回 Long 型数据。
	 * @param statement ： mapper语句id
	 * @param parameters ：参数
	 */
	Long getLong(String statement, Object parameters);
	
	/**
	 * 返回 String 型数据。
	 * @param statement ： mapper语句id
	 * @param parameters ：参数
	 */
	String getString(String statement, Object parameters);
	
	/**
	 * 返回 Object 型数据。
	 * @param statement ： mapper语句id
	 * @param parameters ：参数
	 */
	<T> T getObject(String statement, Object parameters);
	
	/**
	 * 返回 DataRecord 型数据。
	 * @param statement	 ： mapper语句id
	 */
	<T> T getDataRecord(String statement);
	
	/**
	 * 返回 DataRecord 型数据。
	 * @param statement ： mapper语句id
	 * @param parameters ：参数
	 */
	<T> T getDataRecord(String statement, Object parameters);
	
	/**
	 * 按根据表名和主键信息查询，结果返回DataRecord型数据。
	 * @param tableName ：表名
	 * @param primaryKey ：主键名称
	 * @param primaryKeyValue ：主键值
	 * @return
	 */
	<T> T getDataRecord(String tableName, String primaryKey, Serializable primaryKeyValue);
	
	/**
	 * 查询多行数据
	 * @param statement ： mapper语句id
	 * @return
	 */
	<E> List<E> getList(String statement);
	
	/**
	 * 查询多行数据
	 * @param statement ： mapper语句id
	 * @param parameters ：where参数
	 * @return
	 */
	<E> List<E> getList(String statement, Object parameters);
	
	/**
	 * 查询多行数据
	 * @param tableName ：表名
	 * @param parameters ：where参数
	 * @return
	 */
	<E> List<E> getTableList(String tableName, Object parameters);
	
	/**
	 * 删除数据
	 * @param statement ： mapper语句id
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer delete(String statement, SqlSession session) throws DaoException;
	/**
	 * 删除数据
	 * @param statement ： mapper语句id
	 * @return
	 */
	Integer delete(String statement) throws DaoException;
	
	/**
	 * 删除数据
	 * @param statement ： mapper语句id
	 * @param parameters ：where参数
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer delete(String statement, Object parameters, SqlSession session) throws DaoException;
	
	/**
	 * 删除数据
	 * @param statement ： mapper语句id
	 * @param parameters ：where参数
	 * @return
	 */
	Integer delete(String statement, Object parameters) throws DaoException;

	/**
	 * 删除数据
	 * @param tableName ：表名
	 * @param primaryKey ：主键名称
	 * @param primaryKeyValue ：主键值
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer delete(String tableName, String primaryKey, Serializable primaryKeyValue, SqlSession session) throws DaoException;
	
	/**
	 * 删除数据
	 * @param tableName ：表名
	 * @param primaryKey ：主键名称
	 * @param primaryKeyValue ：主键值
	 * @return
	 */
	Integer delete(String tableName, String primaryKey, Serializable primaryKeyValue) throws DaoException;
	 
	/**
	 * 批量删除数据
	 * @param tableName ：表名
	 * @param primaryColumn ：主键名称
	 * @param primaryKeyValues ：主键值，多个用逗号隔开
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer deletes(String tableName, String primaryKey, String primaryKeyValues, SqlSession session) throws DaoException;
	
	/**
	 * 批量删除数据
	 * @param tableName ：表名
	 * @param primaryColumn ：主键名称
	 * @param primaryKeyValues ：主键值，多个用逗号隔开
	 * @return
	 */
	Integer deletes(String tableName, String primaryKey, String primaryKeyValues) throws DaoException;
	
	/**
	 * 批量删除数据
	 * @param tableName ：表名
	 * @param parameters ：where参数
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer deletes(String tableName, Object parameters, SqlSession session) throws DaoException;
	
	/**
	 * 批量删除数据
	 * @param tableName ：表名
	 * @param parameters ：where参数
	 * @return
	 */
	Integer deletes(String tableName, Object parameters) throws DaoException;
	
	/**
	 * 更新数据
	 * @param statement ： mapper语句id
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer update(String statement, SqlSession session) throws DaoException;
	
	/**
	 * 更新数据
	 * @param statement ： mapper语句id
	 * @return
	 * @throws DaoException 
	 */
	Integer update(String statement) throws DaoException;
	
	/**
	 * 更新数据
	 * @param statement ： mapper语句id
	 * @param parameters ：where参数
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer update(String statement, Object parameters, SqlSession session) throws DaoException;
	
	/**
	 * 更新数据
	 * @param statement ： mapper语句id
	 * @param parameters ：where参数
	 * @return
	 */
	Integer update(String statement, Object parameters) throws DaoException;
	
	/**
	 * 更新数据
	 * @param tableName ：表名
	 * @param dataRecord ：要更新到数据库的dataRecord记录
	 * @param parameters ：where参数
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer update(String tableName, DataRecord dataRecord, Map<String, Object> parameters, SqlSession session) throws DaoException;
	
	/**
	 * 更新数据
	 * @param tableName ：表名
	 * @param dataRecord ：要更新到数据库的dataRecord记录
	 * @param parameters ：where参数
	 * @return
	 */
	Integer update(String tableName, DataRecord dataRecord, Map<String, Object> parameters) throws DaoException;
	
	/**
	 * 更新数据
	 * @param tableName ：表名
	 * @param primaryKey ：主键名称
	 * @param primaryKeyValue ：主键值
	 * @param dataRecord ：要更新到数据库的dataRecord记录
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer update(String tableName, String primaryKey, Serializable primaryKeyValue, DataRecord dataRecord, SqlSession session) throws DaoException;
	
	/**
	 * 更新数据
	 * @param tableName ：表名
	 * @param primaryKey ：主键名称
	 * @param primaryKeyValue ：主键值
	 * @param dataRecord ：要更新到数据库的dataRecord记录
	 * @return
	 */
	Integer update(String tableName, String primaryKey, Serializable primaryKeyValue, DataRecord dataRecord) throws DaoException;
	
	/**
	 * 插入数据
	 * @param statement ： mapper语句id
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer insert(String statement, SqlSession session) throws DaoException;
	
	/**
	 * 插入数据
	 * @param statement ： mapper语句id
	 * @return
	 */
	Integer insert(String statement) throws DaoException;
	
	/**
	 * 插入数据
	 * @param tableName ：表名
	 * @param dataRecord ：要插入到数据库的dataRecord记录
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer insert(String tableName, DataRecord dataRecord, SqlSession session) throws DaoException;
	
	/**
	 * 插入数据
	 * @param tableName ：表名
	 * @param dataRecord ：要插入到数据库的dataRecord记录
	 * @return
	 */
	Integer insert(String tableName, DataRecord dataRecord) throws DaoException;
	
	/**
	 * 插入数据
	 * @param statement ： mapper语句id
	 * @param parameters  ：where参数
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer insert(String statement, Object parameters, SqlSession session) throws DaoException;
	
	/**
	 * 插入数据
	 * @param statement ： mapper语句id
	 * @param parameters  ：where参数
	 * @return
	 */
	Integer insert(String statement, Object parameters) throws DaoException;
	
	/**
	 * 批量插入，里面不会对参数进行检验了，底层都是直接拼参数的，要防止 sql 注入的话请提前做好处理
	 * @param lsit ：数据集
	 * @param tableName ：表名
	 * @param session ：可为空，为空时单个方法自动开启 session 并提交，不为空时方便外部开启 session 后执行多次操作
	 * @return
	 */
	Integer insertList(List<DataRecord> list, String tableName, SqlSession session) throws DaoException;
	
	/**
	 * 批量插入，里面不会对参数进行检验了，底层都是直接拼参数的，要防止 sql 注入的话请提前做好处理
	 * @param lsit ：数据集
	 * @param tableName ：表名
	 * @return
	 */
	Integer insertList(List<DataRecord> list, String tableName) throws DaoException;

	/**
	 * 获取记录数
	 * @param tableName ：表名
	 * @param parameters ：where参数
	 * @return
	 */
	Long getCount(String tableName, Map<String, Object> parameters);
	
	/**
	 * 获取一个不会自动提交提交的事务的 SqlSession，需要手动执行 session.commit() 和 session.close() 
	 * 适用于多个方法对单/多张表同时执行操作
	 * @return
	 */
	SqlSession getSession();

}
