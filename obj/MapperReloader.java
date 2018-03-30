package cn.wizzer.app.wb.modules.common.nutzMybatis.obj;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutMvcContext;

import cn.wizzer.app.wb.modules.common.nutzMybatis.config.SqlSessionFactoryBean;
import cn.wizzer.app.wb.modules.common.nutzMybatis.utils.ResourceUtil;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月12日 下午1:11:18
 */
@WebListener
public class MapperReloader implements ServletContextListener, Runnable {
	
	private static SqlSessionFactory sqlSessionFactory;
	private static SqlSessionFactoryBean sqlSessionFactoryBean;
	private static String[] mappers = ResourceUtil.getMappers(); 
	
    @Override  
    public void contextInitialized(ServletContextEvent sce) {
    	if (isDebug()) {//判断在 debug 模式下才做重新加载 mapper 文件的操作
    		Thread thread = new Thread(new MapperReloader());
    		thread.start();
		}
    }
    
	@Override
	public void run() {
		List<String> mapperDirs = ResourceUtil.getMapperDirs();
		if (mapperDirs != null && mapperDirs.size() > 0) {
			WatchService watcher = null;
			try {
				watcher = FileSystems.getDefault().newWatchService();
				for (String dir : mapperDirs) {
					System.out.println("监控mapper目录：" + dir);
					Paths.get(dir).register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
				}
				long formerly = 0;
				while (true) {  
					WatchKey key = watcher.take();
					for (WatchEvent<?> event: key.pollEvents()) {  
		                long current = System.currentTimeMillis();
		                if (current - formerly > 10000 && "ENTRY_MODIFY".equals(event.kind().toString())) {//10秒中之内不重复 load mapper 文件
		                	try {
		                		reloadXML(event.context().toString());
		                	} catch (Exception e1) {
		                		e1.printStackTrace();
		                	}  
		                	formerly = current;
		                }
		            }
					if (!key.reset()) {  
						break;  
					}  
				} 
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}  
		}
	}
	
	private boolean isDebug() {
		List<String> args = ManagementFactory.getRuntimeMXBean().getInputArguments();
    	boolean isDebug = false;
    	for (String arg : args) {
    		if (arg.startsWith("-agentlib:jdwp")) {
    			isDebug = true;
    			break;
    	 	}
    	}
		return isDebug;
	}
	
	public void reloadXML(String fileName) throws Exception {
        Configuration configuration = getSqlSessionFactory().getConfiguration();
        for (String resource : mappers) {
        	if (resource.endsWith(fileName)) {
        		// 清理已加载的资源标识，方便让它重新加载。  
        		removeConfig(configuration, resource);
        		try {
        			XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(Resources.getResourceAsStream(resource),
        					configuration, resource, configuration.getSqlFragments());
        			xmlMapperBuilder.parse();
        			System.out.println("解析完毕：" + resource);
        		} catch (Exception e) {
        			throw new IOException("Failed to parse mapping resource: '" + resource + "'", e);
        		} finally {
        			ErrorContext.instance().reset();
        		}
			}
        }
    }
	
	@SuppressWarnings("unchecked")
	public void clearMappedStatements(Class<?> classConfig, Configuration configuration, String resource) throws Exception {
    	System.out.println("清除mappedStatements");
        Field field = classConfig.getDeclaredField("mappedStatements");
        field.setAccessible(true);
        Map<String, ?> map = (Map<String, ?>)field.get(configuration);
        
        SAXReader reader = new SAXReader();
		reader.setValidation(false);
		reader.setEntityResolver(new IgnoreDTDEntityResolver());
		Document doc = null;
		try {
			doc = reader.read(ResourceUtil.getClassPath() + resource);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = doc.getRootElement();
		String namespace = root.attribute("namespace").getValue();
		List<Object> list = root.content();
		for (Object object : list) {
			if (object instanceof DefaultElement) {
				DefaultElement defaultElement = (DefaultElement)object;
				System.out.println("移除id：" + namespace + "." + defaultElement.attribute("id").getValue());
				map.remove(namespace + "." + defaultElement.attribute("id").getValue());
				System.out.println("移除id：" + defaultElement.attribute("id").getValue());
				map.remove(defaultElement.attribute("id").getValue());
			}
		}
        field.set(configuration, map);
        //((Map<?, ?>)field.get(configuration)).clear();
    }
	
    private void removeConfig(Configuration configuration, String resource) throws Exception {
        Class<?> classConfig = configuration.getClass();
        clearMappedStatements(classConfig, configuration, resource);
        clearSet(classConfig, configuration, "loadedResources", resource);
        // 因为下面都是空的，所以先不清了
        //clearMap(classConfig, configuration, "caches");
        //clearMap(classConfig, configuration, "resultMaps");
        //clearMap(classConfig, configuration, "parameterMaps");
        //clearMap(classConfig, configuration, "keyGenerators");
        //clearMap(classConfig, configuration, "sqlFragments");
    }
    
//	private void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
//    	System.out.println("清除" + fieldName);
//        Field field = classConfig.getDeclaredField(fieldName);
//        field.setAccessible(true);
//        ((Map<?, ?>)field.get(configuration)).clear();
//    }
    
    private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName, String resource) throws Exception {
    	System.out.println("清除loadedResources");
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Set<?> set = (Set<?>) field.get(configuration);
        //清除单个，并不是清除所有
        set.remove(resource);
        //((Set<?>) field.get(configuration)).clear();
    }
    
    /**
	 * @return the sqlSessionFactoryBean
	 */
	public SqlSessionFactoryBean getSqlSessionFactoryBean() {
		if (sqlSessionFactoryBean == null) {
			Ioc ioc = Mvcs.getIoc();
			if (ioc == null) {
				NutMvcContext context = Mvcs.ctx();
				Set<String> set = context.iocs.keySet();
				for (String key : set) {
					ioc = context.iocs.get(key);//就一个而已
				}
			}
			if (ioc != null) {
				sqlSessionFactoryBean = ioc.get(SqlSessionFactoryBean.class);
			}else {
				try {
					throw new Exception("ioc 为空啊！我也不知道怎么办啊~~");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return sqlSessionFactoryBean;
	}
    
	private SqlSessionFactory getSqlSessionFactory() {
		if (sqlSessionFactory == null) {
			sqlSessionFactory = getSqlSessionFactoryBean().getSqlSessionFactory();
		}
		return sqlSessionFactory;
	}
	
}
