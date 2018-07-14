package com.wong.mutzMybatis.dao.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.SqlSession;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import com.wong.mutzMybatis.config.SqlSessionFactoryBean;
import com.wong.mutzMybatis.dao.BaseDao;
import com.wong.mutzMybatis.dto.DataRecord;
import com.wong.mutzMybatis.exception.DaoException;
import com.wong.mutzMybatis.mapping.BaseMapper;
import com.wong.mutzMybatis.obj.Params;

@IocBean(name="baseDao")
public class BaseDaoImpl extends SqlSessionFactoryBean implements BaseDao {
	
	private static final Log log = Logs.get();
	private final SqlSession nullSession = null;//因为下面的方法有些太类似了，会有参数模糊的情况，所以用这个来区分开
	
	@Override
	public Integer getInt(String statement, Object parameters) {
		Object object = this.getObject(statement, parameters);
		return object == null?null:(Integer)object;
	}

	@Override
	public Long getLong(String statement, Object parameters) {
		Object object = this.getObject(statement, parameters);
		return object == null?null:(Long)object;
	}

	@Override
	public String getString(String statement, Object parameters) {
		Object object = this.getObject(statement, parameters);
		return object == null?null:(String)object;
	}

	@Override
	public <T> T getObject(String statement, Object parameters) {
		T obj = null;
		SqlSession session = getSqlSessionFactory().openSession(true);
		try {
			obj = session.selectOne(statement, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} finally {
			BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
			log.debug("\n执行statement:" + statement);
			log.debug("\n执行sql:" + sqlLog.getSql());
			log.debug("\n参数集:" + sqlLog.getParameterObject());
			log.debug("\n返回结果：" + obj);
			session.close();
		}
		return obj;
	}

	@Override
	public <T> T getDataRecord(String statement) {
		return this.getObject(statement, null);
	}

	@Override
	public <T> T getDataRecord(String statement, Object parameters) {
		return this.getObject(statement, parameters);
	}

	@Override
	public DataRecord getDataRecord(String tableName, String primaryKeyName, Serializable primaryKeyValue) throws DaoException {
//		Map<String, Object> parameters = new HashMap<>();
//		parameters.put("tableName", tableName);
//		parameters.put("primaryKey", primaryKeyName);
//		parameters.put("primaryKeyValue", primaryKeyValue);
//		String statement = "com.wong.base.getDataRecord";
//		return this.getObject(statement, parameters);
		try {
			BaseMapper mapper = getSession().getMapper(BaseMapper.class);
			return mapper.getDataRecord(tableName, primaryKeyName, primaryKeyValue);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public <E> List<E> getList(String statement) {
		return this.getList(statement, null);
	}

	@Override
	public <E> List<E> getList(String statement, Object parameters) {
		List<E> obj = null;
		SqlSession session = getSqlSessionFactory().openSession(true);
		try {
			obj = session.selectList(statement, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} finally {
			BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
			log.debug("\n执行statement:" + statement);
			log.debug("\n执行sql:" + sqlLog.getSql());
			log.debug("\n参数集:" + sqlLog.getParameterObject());
			if (obj != null) {
				for (E e : obj) {
					log.debug("结果集:" + e);
				}
			}
			session.close();
		}
		return obj;
	}
	
	@Override
	public List<DataRecord> getTableList(String tableName, Object parameters) throws DaoException {
//		Map<String, Object> param = new HashMap<>();
//		param.put("tableName", tableName);
//		param.put("parameters", parameters);
//		return this.getList("com.wong.base.getTableList", param);
		try {
			BaseMapper mapper = getSession().getMapper(BaseMapper.class);
			return mapper.getTableList(tableName, parameters);
		} catch (Exception e) {
			throw new DaoException(e);
		}
	}

	@Override
	public Integer delete(String statement, SqlSession session) throws DaoException {
		return this.delete(statement, null, session);
	}
	
	@Override
	public Integer delete(String statement) throws DaoException {
		return this.delete(statement, nullSession);
	}

	@Override
	public Integer delete(String statement, Object parameters, SqlSession session) throws DaoException {
		Integer obj = null;
		if (session == null) {
			session = getSession();
			try {
				obj = session.delete(statement, parameters);
				session.commit();
			} catch (Exception e) {
				log.error(e);
				session.rollback();
				throw new DaoException(e);
			} finally {
				BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
				log.debug("\n执行statement:" + statement);
				log.debug("\n执行sql:" + sqlLog.getSql());
				log.debug("\n参数集:" + sqlLog.getParameterObject());
				log.debug("\n结果集:" + obj);
				session.close();
			}
		}else {
			obj = session.delete(statement, parameters);
		}
		return obj;
	}
	
	@Override
	public Integer delete(String statement, Object parameters) throws DaoException {
		return this.delete(statement, parameters, nullSession);
	}

	@Override
	public Integer delete(String tableName, String primaryKey, Serializable primaryKeyValue, SqlSession session) throws DaoException {
//		Map<String, Object> param = new HashMap<>();
//		param.put("tableName", tableName);
//		param.put("primaryKey", primaryKey);
//		param.put("primaryKeyValue", primaryKeyValue);
//		String statement = "com.wong.base.delete";
//		return this.delete(statement, param, session);
		
		int deleteCount = 0;
		if (session == null) {
			session = getSession();
		}
		try {
			BaseMapper mapper = session.getMapper(BaseMapper.class);
			deleteCount = mapper.delete(tableName, primaryKey, primaryKeyValue);
			session.commit();
		} catch (Exception e) {
			session.rollback();
			throw new DaoException(e);
		} finally {
			session.close();
		}
		return deleteCount;
	}
	
	@Override
	public Integer delete(String tableName, String primaryKey, Serializable primaryKeyValue) throws DaoException {
		return this.delete(tableName, primaryKey, primaryKeyValue, nullSession);
	}

	@Override
	public Integer deletes(String tableName, String primaryKey, String primaryKeyValues, SqlSession session) throws DaoException {
//		Map<String, Object> param = new HashMap<>();
//		param.put("tableName", tableName);
//		param.put("primaryKey", primaryKey);
//		param.put("primaryKeyValues", primaryKeyValues);
//		String statement = "com.wong.base.deleteByIds";
//		return this.delete(statement, param, session);
		int deleteCount = 0;
		if (session == null) {
			session = getSession();
		}
		try {
			BaseMapper mapper = session.getMapper(BaseMapper.class);
			deleteCount = mapper.deleteByIds(tableName, primaryKey, primaryKeyValues);
			session.commit();
		} catch (Exception e) {
			session.rollback();
			throw new DaoException(e);
		} finally {
			session.close();
		}
		return deleteCount;
		
	}
	
	@Override
	public Integer deletes(String tableName, String primaryKey, String primaryKeyValues) throws DaoException {
		return this.delete(tableName, primaryKey, primaryKeyValues, nullSession);
	}
	
	@Override
	public Integer deletes(String tableName, Object parameters, SqlSession session) throws DaoException {
		//return this.delete("com.wong.base.deleteByParam", Params.NEW("tableName", tableName).put("parameters", parameters), session);
		int deleteCount = 0;
		if (session == null) {
			session = getSession();
		}
		try {
			BaseMapper mapper = session.getMapper(BaseMapper.class);
			deleteCount = mapper.deleteByParam(tableName, parameters);
			session.commit();
		} catch (Exception e) {
			session.rollback();
			throw new DaoException(e);
		} finally {
			session.close();
		}
		return deleteCount;
	}
	
	@Override
	public Integer deletes(String tableName, Object parameters) throws DaoException {
		return this.deletes(tableName, parameters, nullSession);
	}

	@Override
	public Integer update(String statement, SqlSession session) throws DaoException {
		return this.update(statement, null, session);
	}
	
	@Override
	public Integer update(String statement) throws DaoException {
		return this.update(statement, nullSession);
	}

	@Override
	public Integer update(String statement, Object parameters, SqlSession session) throws DaoException {
		Integer obj = null;
		if (session == null) {
			session = getSession();
		}
		try {
			obj = session.update(statement, parameters);
			session.commit();
		} catch (Exception e) {
			session.rollback();
			throw new DaoException(e);
		} finally {
			BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
			log.debug("\n执行statement:" + statement);
			log.debug("\n执行sql:" + sqlLog.getSql());
			log.debug("\n参数集:" + sqlLog.getParameterObject());
			log.debug("\n结果集:" + obj);
			session.close();
		}
		return obj;
	}
	
	@Override
	public Integer update(String statement, Object parameters) throws DaoException {
		return this.update(statement, parameters, nullSession);
	}

	@Override
	public Integer update(String tableName, DataRecord record, Map<String, Object> parameters, SqlSession session) throws DaoException {
		if (record == null || parameters == null || parameters.isEmpty()) {
			return 0;
		}
		StringBuffer valuesStatement = new StringBuffer();
		StringBuffer whereStatement = new StringBuffer();

		Map<String, Object> values = new HashMap<String, Object>();
		Set<String> objectKeys = record.keySet();
		for (String key : objectKeys) {
			Object value = record.get(key);
			if(value != null) {
				valuesStatement.append(key);
				valuesStatement.append("=");
				String paramName = "p_" + key;
				valuesStatement.append("#{" + paramName + "}");
				valuesStatement.append(",");
				
				values.put(paramName, value);
			}else{
				valuesStatement.append(key).append("=null,");
			}
		}
		// 删除最后的逗号
		if (valuesStatement.length() > 0) {
			valuesStatement.deleteCharAt(valuesStatement.length() - 1);
		}
		if (parameters != null && !parameters.isEmpty()) {
			Set<String> paramKeys = parameters.keySet();
			for (String key : paramKeys) {
				whereStatement.append(key);
				whereStatement.append("=");
				whereStatement.append(parameters.get(key));
				whereStatement.append(" and ");
			}
			// 删除最后的“ and ”
			if (whereStatement.length() > 0) {
				whereStatement.delete(whereStatement.length() - 5, whereStatement.length());
			}
		}
		
		Params params = Params.NEW("tableName", tableName);
		params.put("values", valuesStatement.toString());
		params.put("where", whereStatement.toString());
		params.putAll(values);
		//return this.update("com.wong.base.update", params, session);
		int updateCount = 0;
		if (session == null) {
			session = getSession();
		}
		try {
			BaseMapper mapper = session.getMapper(BaseMapper.class);
			updateCount = mapper.update(params);
			session.commit();
		} catch (Exception e) {
			session.rollback();
			throw new DaoException(e);
		} finally {
			session.close();
		}
		return updateCount;
	}
	
	@Override
	public Integer update(String tableName, DataRecord record, Map<String, Object> parameters) throws DaoException {
		return this.update(tableName, record, parameters, nullSession);
	}

	@Override
	public Integer update(String tableName, String primaryKey, Serializable primaryKeyValue, DataRecord record, SqlSession session) throws DaoException {
		Map<String, Object> param = new HashMap<>();
		param.put(primaryKey, primaryKeyValue);
		return this.update(tableName, record,  param, session);
	}
	
	@Override
	public Integer update(String tableName, String primaryKey, Serializable primaryKeyValue, DataRecord record) throws DaoException {
		return this.update(tableName, primaryKey, primaryKeyValue, record, nullSession);
	}

	@Override
	public Integer insert(String statement, SqlSession session) throws DaoException {
		return this.insert(statement, null, session);
	}
	
	@Override
	public Integer insert(String statement) throws DaoException {
		return this.insert(statement, nullSession);
	}

	@Override
	public Integer insert(String tableName, DataRecord record, SqlSession session) throws DaoException {
		if (record == null) {
			return 0;
		}
		StringBuffer columnStatement = new StringBuffer(); // 保存列名的语句
		StringBuffer valueStatement = new StringBuffer(); // 保存值的语句
		
		Map<String, Object> values = new HashMap<String, Object>();
		Set<String> keys = record.keySet();
		for (String key : keys) {
			Object value = record.get(key);
			if(value != null) {
				columnStatement.append(key);
				columnStatement.append(',');
				String paramName = "p_" + key;
				//生成值参数名列
				valueStatement.append("#{" + paramName + "}");
				valueStatement.append(",");
				values.put(paramName, value);
			}
		}
		// 删除最后的逗号
		if (columnStatement.length() > 0) {
			columnStatement.deleteCharAt(columnStatement.length() - 1);
		}
		if (valueStatement.length() > 0) {
			valueStatement.deleteCharAt(valueStatement.length() - 1);
		}
		Params params = Params.NEW("tableName", tableName);
		params.put("columns", columnStatement.toString());
		params.put("values", valueStatement.toString());
		params.putAll(values);
		//return this.insert("com.wong.base.insert", params, session);
		int insertCount = 0;
		if (session == null) {
			session = getSession();
		}
		try {
			BaseMapper mapper = session.getMapper(BaseMapper.class);
			insertCount = mapper.insert(params);
			session.commit();
		} catch (Exception e) {
			session.rollback();
			throw new DaoException(e);
		} finally {
			session.close();
		}
		return insertCount;
		
	}
	
	@Override
	public Integer insert(String tableName, DataRecord record) throws DaoException {
		return this.insert(tableName, record, nullSession);
	}

	@Override
	public Integer insert(String statement, Object parameters, SqlSession session) throws DaoException {
		Integer obj = null;
		if (session == null) {
			session = getSession();
		}
		try {
			obj = session.insert(statement, parameters);
			session.commit();
		} catch (Exception e) {
			log.error(e);
			session.rollback();
			throw new DaoException(e);
		} finally {
			BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
			log.debug("\n执行statement:" + statement);
			log.debug("\n执行sql:" + sqlLog.getSql());
			log.debug("\n参数集:" + sqlLog.getParameterObject());
			log.debug("\n结果集：" + obj);
			session.close();
		}
		return obj;
	}
	
	@Override
	public Integer insert(String statement, Object parameters) throws DaoException {
		return this.insert(statement, parameters, null);
	}

	@Override
	public Integer insertList(List<DataRecord> dataList, String tableName, SqlSession session) throws DaoException {
		Map<String, Object> param = new HashMap<>();
		param.put("tableName", tableName);
		param.put("dataList", dataList);
		//String statement = "com.wong.base.insertList";
		
		if(dataList.isEmpty()){
			return 0;
		}
		Set<String> columnSet = dataList.get(0).keySet();
		
		StringBuffer insertColumn = new StringBuffer(); 
		StringBuffer pramaColumn = new StringBuffer();
		
		for (String column : columnSet) {
			insertColumn.append(column);
			insertColumn.append(',');
			pramaColumn.append("#{item.");
			pramaColumn.append(column);
			pramaColumn.append('}');
			pramaColumn.append(',');
		}
		insertColumn.deleteCharAt(insertColumn.length()-1);
		pramaColumn.deleteCharAt(pramaColumn.length()-1);
		
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("tableName", tableName);
		parameters.put("columns", insertColumn);
		parameters.put("items", pramaColumn);
		
		//400条执行一次，避免sql语句过长无法执行
		List<DataRecord> tempList = new ArrayList<DataRecord>();
		
		
		int insertCount = 0;
		if (session == null) {
			session = getSession();
		}
		BaseMapper mapper = session.getMapper(BaseMapper.class);
		try {
			for (int i = 0; i < dataList.size(); i++) {
				tempList.add(dataList.get(i));
				if((i+1) % 400 == 0 || i == dataList.size() - 1) {
					parameters.put("list", tempList);
					//this.insert(statement, parameters, session);
					insertCount += mapper.insertList(parameters);
					
					tempList.clear();
				}
			}
			session.commit();
		} catch (Exception e) {
			session.rollback();
			throw new DaoException(e);
		} finally {
			session.close();
		}
		return insertCount;
	}
	
	@Override
	public Integer insertList(List<DataRecord> dataList, String tableName) throws DaoException {
		return this.insertList(dataList, tableName, nullSession);
	}

	@Override
	public Long getCount(String tableName, Map<String, Object> parameters) throws DaoException {
		//String statement = "com.wong.base.getCount";
		//return this.getLong(statement, Params.NEW("tableName", tableName).put("parameters", parameters));
		BaseMapper mapper = getSession().getMapper(BaseMapper.class);
		try {
			return mapper.getCount(tableName, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DaoException(e);
		}
	}
	
	@Override
	public SqlSession getSession() {
		return getSqlSessionFactory().openSession(false);
	}

}
