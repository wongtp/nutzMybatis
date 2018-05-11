package cn.wizzer.app.wb.modules.common.nutzMybatis.config;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutMvcContext;

import com.alibaba.druid.pool.DruidDataSource;

import cn.wizzer.app.wb.modules.common.nutzMybatis.utils.ResourceUtil;
import cn.wizzer.app.wb.modules.common.nutzMybatis.utils.StringUtil;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月11日 下午7:40:56
 */
//@IocBean(name="sqlSessionFactoryBean")
public class SqlSessionFactoryBean {
	
	private static final Log log = Logs.get();

	private String configLocation = "mybatis-config.xml";
	
	private Configuration configuration;

	private String[] mapperLocations = ResourceUtil.getMappers();
	
	private DataSource dataSource;
	
	private DatabaseIdProvider databaseIdProvider = new VendorDatabaseIdProvider();
	
	private TransactionFactory transactionFactory = new JdbcTransactionFactory();

	private Properties configurationProperties;

	private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

	private static SqlSessionFactory sqlSessionFactory;

	private String environment = SqlSessionFactoryBean.class.getSimpleName();
  
	private Interceptor[] plugins;

	private TypeHandler<?>[] typeHandlers;

	private String typeHandlersPackage;

	private Class<?>[] typeAliases;

	private String typeAliasesPackage;

	private Class<?> typeAliasesSuperType;

	private Class<? extends VFS> vfs;

	private Cache cache;

	private ObjectFactory objectFactory;

	private ObjectWrapperFactory objectWrapperFactory;
	
	public SqlSessionFactoryBean() {
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
				try {
					throw new Exception("ioc 为空啊！我也不知道怎么办啊~~");
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
				}
			}
		}
	}
	
	public DatabaseIdProvider getDatabaseIdProvider() {
		return databaseIdProvider;
	}

	public void setDatabaseIdProvider(DatabaseIdProvider databaseIdProvider) {
		this.databaseIdProvider = databaseIdProvider;
	}

	public Class<? extends VFS> getVfs() {
	    return this.vfs;
	}

	public void setVfs(Class<? extends VFS> vfs) {
	    this.vfs = vfs;
	}

	public Cache getCache() {
	    return this.cache;
	}

	public void setCache(Cache cache) {
	    this.cache = cache;
	}

	public void setPlugins(Interceptor[] plugins) {
		this.plugins = plugins;
	}

	public void setTypeAliasesPackage(String typeAliasesPackage) {
	    this.typeAliasesPackage = typeAliasesPackage;
	}

	public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
	    this.typeAliasesSuperType = typeAliasesSuperType;
	}

	public void setTypeHandlersPackage(String typeHandlersPackage) {
	    this.typeHandlersPackage = typeHandlersPackage;
	}
	  
	public void setTypeHandlers(TypeHandler<?>[] typeHandlers) {
	    this.typeHandlers = typeHandlers;
	}

	public void setTypeAliases(Class<?>[] typeAliases) {
	    this.typeAliases = typeAliases;
	}

	public void setConfigLocation(String configLocation) {
	    this.configLocation = configLocation;
	}

	public void setConfiguration(Configuration configuration) {
	    this.configuration = configuration;
	}

	public void setMapperLocations(String[] mapperLocations) {
	    this.mapperLocations = mapperLocations;
	}

	public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
	    this.configurationProperties = sqlSessionFactoryProperties;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setSqlSessionFactoryBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
	    this.sqlSessionFactoryBuilder = sqlSessionFactoryBuilder;
	}

	public void setTransactionFactory(TransactionFactory transactionFactory) {
	    this.transactionFactory = transactionFactory;
	}

	public void setEnvironment(String environment) {
	    this.environment = environment;
	}

	public void afterPropertiesSet() {
		if (sqlSessionFactoryBuilder == null) {
			throw new IllegalArgumentException("Property 'sqlSessionFactoryBuilder' is required");
		}
		if (!((configuration == null && configLocation == null) || !(configuration != null && configLocation != null))) {
			throw new IllegalStateException("Property 'configuration' and 'configLocation' can not specified with together");
		}
		try {
			sqlSessionFactory = buildSqlSessionFactory();
		} catch (IOException e) {
			log.error(e);
			e.printStackTrace();
		}
	}
	  
	public SqlSessionFactory getSqlSessionFactory() {
		if (sqlSessionFactory == null) {
			synchronized (this) {
				if (sqlSessionFactory == null) {
					try {
						afterPropertiesSet();
					} catch (Exception e) {
						log.error(e);
						e.printStackTrace();
					}
				}
			}
		}
		return sqlSessionFactory;
	}

	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
	    Configuration configuration;
	    XMLConfigBuilder xmlConfigBuilder = null;
	    if (this.configuration != null) {
	    	configuration = this.configuration;
	    	if (configuration.getVariables() == null) {
	    		configuration.setVariables(this.configurationProperties);
	    	} else if (this.configurationProperties != null) {
	    		configuration.getVariables().putAll(this.configurationProperties);
	    	}
	    } else if (this.configLocation != null) {
	    	xmlConfigBuilder = new XMLConfigBuilder(Resources.getResourceAsStream(configLocation), null, this.configurationProperties);
	    	configuration = xmlConfigBuilder.getConfiguration();
	    } else {
	    	if (log.isDebugEnabled()) {
	    		log.debug("Property 'configuration' or 'configLocation' not specified, using default MyBatis Configuration");
	    	}
	    	configuration = new Configuration();
	    	if (this.configurationProperties != null) {
	    		configuration.setVariables(this.configurationProperties);
	    	}
	    }

	    if (this.objectFactory != null) {
	    	configuration.setObjectFactory(this.objectFactory);
	    }

	    if (this.objectWrapperFactory != null) {
	    	configuration.setObjectWrapperFactory(this.objectWrapperFactory);
	    }

	    if (this.vfs != null) {
	    	configuration.setVfsImpl(this.vfs);
	    }

	    if (this.typeAliasesPackage != null && !this.typeAliasesPackage.isEmpty()) {
	    	String[] typeAliasPackageArray = StringUtil.tokenizeToStringArray(this.typeAliasesPackage, ",; \t\n", true, true);
	    	for (String packageToScan : typeAliasPackageArray) {
	    		configuration.getTypeAliasRegistry().registerAliases(packageToScan, typeAliasesSuperType == null ? Object.class : typeAliasesSuperType);
	    		if (log.isDebugEnabled()) {
	    			log.debug("Scanned package: '" + packageToScan + "' for aliases");
	    		}
	    	}
	    }

	    if (this.typeAliases != null && this.typeAliases.length > 0) {
	    	for (Class<?> typeAlias : this.typeAliases) {
	    		configuration.getTypeAliasRegistry().registerAlias(typeAlias);
	    		if (log.isDebugEnabled()) {
	    			log.debug("Registered type alias: '" + typeAlias + "'");
	    		}
	    	}
	    }

	    if (this.plugins != null && this.plugins.length > 0) {
	    	for (Interceptor plugin : this.plugins) {
	    		configuration.addInterceptor(plugin);
	    		if (log.isDebugEnabled()) {
	    			log.debug("Registered plugin: '" + plugin + "'");
	    		}
	    	}
	    }

	    if (this.typeHandlersPackage != null && !this.typeHandlersPackage.isEmpty()) {
	    	String[] typeHandlersPackageArray = StringUtil.tokenizeToStringArray(this.typeHandlersPackage, ",; \t\n", true, true);
	    	for (String packageToScan : typeHandlersPackageArray) {
	    		configuration.getTypeHandlerRegistry().register(packageToScan);
	    		if (log.isDebugEnabled()) {
	    			log.debug("Scanned package: '" + packageToScan + "' for type handlers");
	    		}
	    	}
	    }

	    if (this.typeHandlers != null && this.typeHandlers.length > 0) {
	    	for (TypeHandler<?> typeHandler : this.typeHandlers) {
	    		configuration.getTypeHandlerRegistry().register(typeHandler);
	    		if (log.isDebugEnabled()) {
	    			log.debug("Registered type handler: '" + typeHandler + "'");
	    		}
	    	}
	    }
	    
	    if (this.dataSource == null) {
	    	throw new IOException("dataSourcePropsLocatetion is null");
	    }
	    if (this.databaseIdProvider != null) {//fix #64 set databaseId before parse mapper xmls
	    	try {
	    		configuration.setDatabaseId(this.databaseIdProvider.getDatabaseId(this.dataSource));
	    	} catch (SQLException e) {
	    		throw new IOException("Failed getting a databaseId", e);
	    	}
	    }
	    if (this.cache != null) {
	    	configuration.addCache(this.cache);
	    }

	    if (xmlConfigBuilder != null) {
	    	try {
	    		xmlConfigBuilder.parse();
	    		if (log.isDebugEnabled()) {
	    			log.debug("Parsed configuration file: '" + this.configLocation + "'");
	    		}
	    	} catch (Exception ex) {
	    		throw new IOException("Failed to parse config resource: " + this.configLocation, ex);
	    	} finally {
	    		ErrorContext.instance().reset();
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
	    return this.sqlSessionFactoryBuilder.build(configuration);
	}
	
}

