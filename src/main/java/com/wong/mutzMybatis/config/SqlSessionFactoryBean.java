package com.wong.mutzMybatis.config;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.JdbcType;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutMvcContext;

import com.alibaba.druid.pool.DruidDataSource;
import com.wong.mutzMybatis.dto.DataRecord;
import com.wong.mutzMybatis.mapping.BaseMapper;
import com.wong.mutzMybatis.utils.ResourceUtil;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月11日 下午7:40:56
 */
@IocBean(name="sqlSessionFactoryBean")
public class SqlSessionFactoryBean {
	
	private static final Log log = Logs.get();

	private String[] mapperLocations = ResourceUtil.getMappers();
	
	private DataSource dataSource;
	
	private DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
	
	private TransactionFactory transactionFactory = new JdbcTransactionFactory();

	private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

	private SqlSessionFactory sqlSessionFactory;

	private String environment = SqlSessionFactoryBean.class.getSimpleName();

	private DataSource getDataSource() throws IOException {
		if (dataSource == null) {
			Ioc ioc = Mvcs.getIoc();
			if (ioc == null) {
				NutMvcContext context = Mvcs.ctx();
				Set<String> set = context.iocs.keySet();
				for (String key : set) {
					ioc = context.iocs.get(key);//就一个而已
				}
			}
			if (ioc != null) {
				dataSource = ioc.get(DruidDataSource.class, "dataSource");
			}else {
				log.error("ioc 为空啊！我也不知道怎么办啊~~");
			}
			if (dataSource == null) {
				throw new IOException("dataSource is null ");
			}
		}
		return dataSource;
	}
	
	/**
	 * 用 java 类的形式配置 base mapper
	 * @param configuration
	 */
	private void setBaseMapper(Configuration configuration) {
		configuration.addMapper(BaseMapper.class);
	}
	
	private Configuration getConfiguration() {
		//强制采用下面的默认配置，暂时不改了
		Configuration config = new Configuration();
		config.setCacheEnabled(true);
		config.setLazyLoadingEnabled(false);
		config.setAggressiveLazyLoading(true);
		config.setJdbcTypeForNull(JdbcType.NULL);
		config.setCallSettersOnNulls(true);
		config.getTypeAliasRegistry().registerAlias("dataRecord", DataRecord.class);
		
		setBaseMapper(config);
		return config;
	}
	
	public SqlSessionFactory getSqlSessionFactory() {
		if (this.sqlSessionFactory == null) {
			try {
				this.sqlSessionFactory = buildSqlSessionFactory();
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		}
		return this.sqlSessionFactory;
	}
	
	public SqlSessionFactory buildSqlSessionFactory(Configuration configuration) throws IOException {
		this.sqlSessionFactory = this.sqlSessionFactoryBuilder.build(configuration);
	    return this.sqlSessionFactory;
	}
	
	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
	    Configuration configuration = getConfiguration();
	    this.dataSource = getDataSource();
	    if (this.dataSource == null) {
	    	throw new IOException("dataSource is null");
	    }
	    if (this.databaseIdProvider != null) {
	    	try {
	    		configuration.setDatabaseId(this.databaseIdProvider.getDatabaseId(this.dataSource));
	    	} catch (SQLException e) {
	    		throw new IOException("Failed getting a databaseId", e);
	    	}
	    }
	    configuration.setEnvironment(new Environment(this.environment, this.transactionFactory, this.dataSource));
	    if (this.mapperLocations != null) {
	    	for (String mapperLocation : this.mapperLocations) {
	    		if (mapperLocation == null) {
	    			continue;
	    		}
	    		try {
	    			XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(Resources.getResourceAsStream(mapperLocation),
	    				configuration, mapperLocation.toString(), configuration.getSqlFragments());
	    			xmlMapperBuilder.parse();
	    		} catch (Exception e) {
	    			throw new IOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
	    		} finally {
	    			ErrorContext.instance().reset();
	    		}

	    		if (log.isDebugEnabled()) {
	    			log.debug("Parsed mapper file: '" + mapperLocation + "'");
	    		}
	    	}
	    } else {
	    	if (log.isDebugEnabled()) {
	    		log.debug("Property 'mapperLocations' was not specified or no matching resources found");
	    	}
	    }
	    return buildSqlSessionFactory(configuration);
	}
}

