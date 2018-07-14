package com.wong.mutzMybatis;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.wong.mutzMybatis.config.SqlSessionFactoryBean;
import com.wong.mutzMybatis.dao.BaseDao;
import com.wong.mutzMybatis.dao.impl.BaseDaoImpl;
import com.wong.mutzMybatis.dto.DataRecord;
import com.wong.mutzMybatis.exception.DaoException;
import com.wong.mutzMybatis.mapping.BaseMapper;
import com.wong.mutzMybatis.obj.Params;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月10日 下午8:27:07
 */
public class Tester {
	DruidDataSource druidDataSource;
	
	//
	//测试是否成功连接到数据库平查询到数据
	public static void main(String[] args) {
		//new Tester().testBaseMapperInsert();
		//new Tester().testBaseMapperUpdate();
		//new Tester().testBaseMapperInsertList();
		new Tester().testBaseMapperGetTableList();
		
	}
	
	public void testBaseMapper() {
		BaseMapper mapper = getSession().getMapper(BaseMapper.class);
		List<DataRecord> result;
		try {
			result = mapper.getTableList("wb_ad", Params.NEW("2", 2).put("3", 3));
			System.out.println("Result: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testBaseMapperInsert() {
		BaseDao baseDao = new BaseDaoImpl();
		DataRecord dRecord = new DataRecord();
		dRecord.put("id", 2);
		dRecord.put("picPath", "http:asdfasdf/adf.com");
		dRecord.put("targetUrl", "http:asdfa6546465431321321321321321321df.com");
		dRecord.put("createAt", 2);
		dRecord.put("createBy", "aasdfdsdf");
		dRecord.put("opAt", 2);
		dRecord.put("delFlag", false);
		try {
			baseDao.insert("wb_ad", dRecord, getSession());
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	public void testBaseMapperInsertList() {
		BaseDao baseDao = new BaseDaoImpl();
		DataRecord dRecord4 = new DataRecord();
		dRecord4.put("id", 6);
		dRecord4.put("picPath", "http:asdfasdf/adf.com");
		dRecord4.put("targetUrl", "http:asdfa6546465431321321321321321321df.com");
		dRecord4.put("createAt", 2);
		dRecord4.put("createBy", "aasdfdsdf");
		dRecord4.put("opAt", 2);
		dRecord4.put("delFlag", false);
		
		DataRecord dRecord3 = new DataRecord();
		dRecord3.put("id", 3);
		dRecord3.put("picPath", "http:asdfasdf/adf.com");
		dRecord3.put("targetUrl", "http:asdfa6546465431321321321321321321df.com");
		dRecord3.put("createAt", 2);
		dRecord3.put("createBy", "aasdfdsdf");
		dRecord3.put("opAt", 2);
		dRecord3.put("delFlag", false);
		
		DataRecord dRecord2 = new DataRecord();
		dRecord2.put("id", 4);
		dRecord2.put("picPath", "http:asdfasdf/adf.com");
		dRecord2.put("targetUrl", "http:asdfa6546465431321321321321321321df.com");
		dRecord2.put("createAt", 2);
		dRecord2.put("createBy", "aasdfdsdf");
		dRecord2.put("opAt", 2);
		dRecord2.put("delFlag", false);
		
		DataRecord dRecord1 = new DataRecord();
		dRecord1.put("id", 5);
		dRecord1.put("picPath", "http:asdfasdf/adf.com");
		dRecord1.put("targetUrl", "http:asdfa6546465431321321321321321321df.com");
		dRecord1.put("createAt", 2);
		dRecord1.put("createBy", "aasdfdsdf");
		dRecord1.put("opAt", 2);
		dRecord1.put("delFlag", false);
		
		List<DataRecord> list = new ArrayList<>();
		list.add(dRecord1);
		list.add(dRecord2);
		list.add(dRecord3);
		list.add(dRecord4);
		
		try {
			//baseDao.insert("wb_ad", dRecord, getSession());
			baseDao.insertList(list, "wb_ad", getSession());
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	public void testBaseMapperUpdate() {
		BaseDao baseDao = new BaseDaoImpl();
		DataRecord dRecord = new DataRecord();
		dRecord.put("picPath", "http:.com");
		dRecord.put("targetUrl", "http:.com");
		dRecord.put("createAt", null);
		dRecord.put("opAt", 2);
		dRecord.put("delFlag", true);
		try {
			baseDao.update("wb_ad", dRecord, Params.NEW("id", "2"), getSession());
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	public void testBaseMapperGetTableList() {
		try {
			try {
//				BaseMapper mapper = getSession().getMapper(BaseMapper.class);
//				DataRecord dRecord = mapper.getDataRecord("wb_ad", "id", "2");
//				System.out.println(dRecord.toString());
				BaseDao baseDao = new BaseDaoImpl();
				baseDao.deletes("wb_ad", Params.NEW("createBy", "aasdfdsdf"), getSession());
				
			} catch (Exception e) {
				throw new DaoException(e);
			}
		} catch (DaoException e) {
			e.printStackTrace();
		}
	}
	
	public SqlSession getSession() {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.DataSource(getDataSource());
		SqlSessionFactory sqlSessionFactory = null;
	    SqlSession session = null;
		try {
	        sqlSessionFactory = bean.getSqlSessionFactory();
	        session = sqlSessionFactory.openSession(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}	
	
	private static String dataSourcePropsLocatetion = "config/custom/db.properties";
	private DruidDataSource getDataSource() {
		if (druidDataSource == null) {
			DruidDataSource dataSource = new DruidDataSource();//默认使用 dataSource
			Properties dataSourceProps = null;
			try {
				dataSourceProps = Resources.getResourceAsProperties(dataSourcePropsLocatetion);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			dataSource.configFromPropety(dataSourceProps);
			dataSource.setDriverClassName(dataSourceProps.getProperty("db.driverClassName"));
			dataSource.setUrl(dataSourceProps.getProperty("db.url"));
			dataSource.setUsername(dataSourceProps.getProperty("db.username"));
			dataSource.setPassword(dataSourceProps.getProperty("db.password"));
			dataSource.setRemoveAbandonedTimeout(Integer.valueOf(dataSourceProps.getProperty("db.removeAbandonedTimeout")));
			dataSource.setRemoveAbandoned(Boolean.valueOf(dataSourceProps.getProperty("db.removeAbandoned")));
			dataSource.setConnectionProperties(dataSourceProps.getProperty("db.connectionProperties"));
			dataSource.setMaxActive(Integer.valueOf(dataSourceProps.getProperty("db.maxActive")));
			dataSource.setValidationQuery(dataSourceProps.getProperty("db.validationQuery"));
			dataSource.setDefaultAutoCommit(Boolean.valueOf(dataSourceProps.getProperty("db.defaultAutoCommit")));
			
			dataSource.configFromPropety(dataSourceProps);
			dataSource.setConnectProperties(dataSourceProps);
			try {
				dataSource.init();
				this.druidDataSource = dataSource;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		return druidDataSource;
	}
	
}
