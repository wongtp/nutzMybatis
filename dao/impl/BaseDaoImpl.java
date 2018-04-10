package cn.wizzer.app.wb.modules.common.nutzMybatis.dao.impl;

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

import cn.wizzer.app.wb.modules.common.nutzMybatis.config.SqlSessionFactoryBean;
import cn.wizzer.app.wb.modules.common.nutzMybatis.dao.BaseDao;
import cn.wizzer.app.wb.modules.common.nutzMybatis.dto.DataRecord;

@IocBean(name="baseDao")
public class BaseDaoImpl extends SqlSessionFactoryBean implements BaseDao {
	
	private static final Log log = Logs.get();
	private SqlSession sqlSession;
	
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
		SqlSession session = getSqlSessionFactory().openSession();
		try {
			obj = session.selectOne(statement, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} finally {
			if (log.isDebugEnabled()) {
				BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
				log.debug("\n执行sql:" + sqlLog.getSql());
				log.debug("\n参数集合:" + sqlLog.getParameterObject());
				log.debug("\n返回结果：" + obj);
			}
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
	public <T> T getDataRecord(String tableName, String primaryKey, Serializable primaryKeyValue) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("TABLE_NAME", tableName);
		parameters.put("PRIMARY_KEY", primaryKey);
		parameters.put("PRIMARY_KEY_VALUE", primaryKeyValue);
		String statement = "com.wong.base.getDataRecord";
		return this.getObject(statement, parameters);
	}

	@Override
	public <E> List<E> getList(String statement) {
		return this.getList(statement, null);
	}

	@Override
	public <E> List<E> getList(String statement, Object parameters) {
		List<E> obj = null;
		SqlSession session = getSqlSessionFactory().openSession();
		try {
			obj = session.selectList(statement, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		} finally {
			if (log.isDebugEnabled()) {
				BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
				log.debug("\n执行sql:" + sqlLog.getSql());
				log.debug("\n参数集合:" + sqlLog.getParameterObject());
				if (obj != null) {
					for (E e : obj) {
						log.debug("\n结果集:" + e);
					}
				}
			}
			session.close();
		}
		return obj;
	}
	
	@Override
	public <E> List<E> getTableList(String tableName, Object parameters) {
		Map<String, Object> param = new HashMap<>();
		param.put("TABLE_NAME", tableName);
		param.put("PARAMETERS", parameters);
		return this.getList("com.wong.base.getTableList", param);
	}

	@Override
	public Integer delete(String statement, SqlSession session) {
		return this.delete(statement, null, session);
	}

	@Override
	public Integer delete(String statement, Object parameters, SqlSession session) {
		Integer obj = null;
		if (session == null) {
			session = getSqlSessionFactory().openSession();
			try {
				obj = session.delete(statement, parameters);
				session.commit();
			} catch (Exception e) {
				session.rollback();
				e.printStackTrace();
				log.error(e);
			} finally {
				if (log.isDebugEnabled()) {
					BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
					log.debug("\n执行sql:" + sqlLog.getSql());
					log.debug("\n参数集合:" + sqlLog.getParameterObject());
					log.debug("\n结果集:" + obj);
				}
				session.close();
			}
		}else {
			obj = session.delete(statement, parameters);
		}
		return obj;
	}

	@Override
	public Integer delete(String tableName, String primaryKey, Serializable primaryKeyValue, SqlSession session) {
		Map<String, Object> param = new HashMap<>();
		param.put("TABLE_NAME", tableName);
		param.put("PRIMARY_KEY", primaryKey);
		param.put("PRIMARY_KEY_VALUE", primaryKeyValue);
		String statement = "com.wong.base.delete";
		return this.delete(statement, param, session);
	}

	@Override
	public Integer deletes(String tableName, String primaryKey, String primaryKeyValues, SqlSession session) {
		Map<String, Object> param = new HashMap<>();
		param.put("TABLE_NAME", tableName);
		param.put("PRIMARY_KEY", primaryKey);
		param.put("PRIMARY_KEY_VALUE", primaryKeyValues);
		String statement = "com.wong.base.deleteByIDs";
		return this.delete(statement, param, session);
	}
	
	@Override
	public Integer deletes(String tableName, Object parameters, SqlSession session) {
		Map<String, Object> param = new HashMap<>();
		param.put("TABLE_NAME", tableName);
		param.put("PARAMETERS", parameters);
		String statement = "com.wong.base.deleteByParam";
		return this.delete(statement, param, session);
	}

	@Override
	public Integer update(String statement, SqlSession session) {
		return this.update(statement, null, session);
	}

	@Override
	public Integer update(String statement, Object parameters, SqlSession session) {
		Integer obj = null;
		if (session == null) {
			session = getSqlSessionFactory().openSession();
			try {
				obj = session.update(statement, parameters);
				session.commit();
			} catch (Exception e) {
				session.rollback();
				e.printStackTrace();
				log.error(e);
			} finally {
				if (log.isDebugEnabled()) {
					BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
					log.debug("\n执行sql:" + sqlLog.getSql());
					log.debug("\n参数集合:" + sqlLog.getParameterObject());
					log.debug("\n结果集:" + obj);
				}
				session.close();
			}
		}else {
			obj = session.update(statement, parameters);
		}
		return obj;
	}

	@Override
	public Integer update(String tableName, DataRecord record, Map<String, Object> parameters, SqlSession session) {
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
				String paramName = "PARAM_" + key;
				values.put(paramName, value);
				valuesStatement.append("#{" + paramName + "}");
				valuesStatement.append(",");
			}else{
				valuesStatement.append(key).append("=null,");
			}
		}
		// 删除最后的半角逗号
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
			if (whereStatement.length() > 0) {
				//whereStatement.deleteCharAt(whereStatement.length() - 1);
				whereStatement.delete(whereStatement.length() - 5, whereStatement.length());
			}
		}
		Map<String, Object> updateParams = new HashMap<String, Object>();
		updateParams.put("TABLENAME_STATEMENT", tableName);
		updateParams.put("VALUES_STATEMENT", valuesStatement.toString());
		updateParams.put("WHERE_STATEMENT", whereStatement.toString());
		updateParams.putAll(values);
		String statement = "com.wong.base.update";
		return this.update(statement, updateParams, session);
	}

	@Override
	public Integer update(String tableName, String primaryKey, Serializable primaryKeyValue, DataRecord record, SqlSession session) {
		Map<String, Object> param = new HashMap<>();
		param.put(primaryKey, primaryKeyValue);
		return this.update(tableName, record,  param, session);
	}

	@Override
	public Integer insert(String statement, SqlSession session) {
		return this.insert(statement, null, session);
	}

	@Override
	public Integer insert(String tableName, DataRecord record, SqlSession session) {
		if (record == null) {
			return 0;
		}
		StringBuffer columnStatement = new StringBuffer(); // 保存列名的语句
		StringBuffer valueStatement = new StringBuffer(); // 保存值的语句

		Map<String, Object> values = new HashMap<String, Object>();
		Set<String> keys = record.keySet();
		for (String key : keys) {
			//Object value = convertToDbType(tableName, key, object.get(key));
			Object value = record.get(key);
			if(value != null) {
				columnStatement.append(key);
				columnStatement.append(',');
				String paramName = "PARAM_" + key;
				//生成值参数名列表
				valueStatement.append("#{" + paramName + "}");
				valueStatement.append(",");
				values.put(paramName, value);
			}
		}
		// 删除最后的半角逗号
		if (columnStatement.length() > 0) {
			columnStatement.deleteCharAt(columnStatement.length() - 1);
		}
		if (valueStatement.length() > 0) {
			valueStatement.deleteCharAt(valueStatement.length() - 1);
		}
		// 提供参数
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("TABLE_NAME", tableName);
		parameters.put("COLUMNS", columnStatement.toString());
		parameters.put("VALUES", valueStatement.toString());
		parameters.putAll(values);
		String statement = "com.wong.base.insert";
		return this.insert(statement, parameters, session);
	}

	@Override
	public Integer insert(String statement, Object parameters, SqlSession session) {
		Integer obj = null;
		if (session == null) {
			session = getSqlSessionFactory().openSession();
			try {
				obj = session.insert(statement, parameters);
				session.commit();
			} catch (Exception e) {
				session.rollback();
				e.printStackTrace();
				log.error(e);
			} finally {
				if (log.isDebugEnabled()) {
					BoundSql sqlLog = session.getConfiguration().getMappedStatement(statement).getBoundSql(parameters);
					log.debug("\n执行sql:" + sqlLog.getSql());
					log.debug("\n参数集合:" + sqlLog.getParameterObject());
					log.debug("\n结果集：" + obj);
				}
				session.close();
			}
		}else {
			obj = session.insert(statement, parameters);
		}
		return obj;
	}

	@Override
	public Integer insertList(List<DataRecord> dataList, String tableName, SqlSession session) {
		Map<String, Object> param = new HashMap<>();
		param.put("TABLE_NAME", tableName);
		param.put("DATA_LIST", dataList);
		String statement = "com.wong.base.insertList";
		
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
		parameters.put("TABLENAME", tableName);
		parameters.put("COLUMNS", insertColumn);
		parameters.put("ITEMS", pramaColumn);
		
		int count=0;
		//500条执行一次，避免sql语句过长，导致无法执行
		List<DataRecord> tempList = new ArrayList<DataRecord>();
		for (int i = 0; i < dataList.size(); i++) {
			tempList.add(dataList.get(i));
			if((i+1) % 500 == 0 || i == dataList.size() - 1) {
				parameters.put("LIST", tempList);
				count+= this.insert(statement, parameters, session);
				tempList.clear();
			}
		}
		return count;
	}

	@Override
	public Long getCount(String tableName, Map<String, Object> parameters) {
		Map<String, Object> param = new HashMap<>();
		param.put("TABLE_NAME", tableName);
		param.put("PARAMETERS", parameters);
		String statement = "com.wong.base.getCount";
		return this.getLong(statement, param);
	}
	
	@Override
	public SqlSession getSession() {
		if (sqlSession == null) {
			sqlSession = getSqlSessionFactory().openSession();
		}
		return sqlSession;
	}

}
