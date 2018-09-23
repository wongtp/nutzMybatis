package com.wong.mapperReloader;

import java.io.IOException;
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
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutMvcContext;

import com.wong.mutzMybatis.config.SqlSessionFactoryBean;
import com.wong.mutzMybatis.obj.IgnoreDTDEntityResolver;
import com.wong.mutzMybatis.utils.ResourceUtil;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月12日 下午1:11:18
 */
@WebListener
public class MapperReloader implements ServletContextListener, Runnable {
	
	private static String[] mappers = ResourceUtil.getMappers();
	
    @Override  
    public void contextInitialized(ServletContextEvent sce) {
		//新开一条线程，而不至于阻塞
		Thread thread = new Thread(new MapperReloader());
		thread.setDaemon(true); // 设置为守护线程
		thread.start();
    }
    
	@Override
	public void run() {
		//获取所有以 -mapper.xml 结尾文件的目录
		List<String> mapperDirs = ResourceUtil.getMapperDirs();
		if (mapperDirs != null && mapperDirs.size() > 0) {
			try {
				//开启一个文件监视器
				WatchService watcher = FileSystems.getDefault().newWatchService();
				//把所有要监控的目录都注册进来
				for (String dir : mapperDirs) {
					System.out.println("监控mapper目录：" + dir);
					Paths.get(dir).register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
				}
				
				long formerly = 0;
				WatchKey key;
				while (true) {  
					key = watcher.take();
					for (WatchEvent<?> event: key.pollEvents()) {  
		                long current = System.currentTimeMillis();
		                //10秒内防止重复加载 mapper 文件，因为这个监视器会跳出来两次文件被修改的信号； 同样这些目录都是只监控文件的修改就行了，创建和删除好像没必要
		                if (current - formerly > 10000 && "ENTRY_MODIFY".equals(event.kind().toString())) {
		                	reloadXML(event.context().toString());
		                	formerly = current;
		                }
		            }
					if (!key.reset()) {  
						break;  
					}  
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void reloadXML(String fileName) {
        Configuration configuration = getSqlSessionFactoryBean().getSqlSessionFactory().getConfiguration();
        for (String resource : mappers) {
        	//因为获取不到具体的路径，只有把所有同名的 mapper 文件都加载一遍了
        	if (resource.endsWith(fileName)) {
        		// 清理已加载的资源标识，方便让它重新加载。  
        		try {
        			removeConfig(configuration, resource);
        			XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(Resources.getResourceAsStream(resource),
        					configuration, resource, configuration.getSqlFragments());
        			xmlMapperBuilder.parse();
        			System.out.println("解析完毕：" + resource);
        		} catch (Exception e) {
        			e.printStackTrace();
        			System.out.println("Failed to parse mapping resource: '" + resource + "'");
        		} finally {
        			ErrorContext.instance().reset();
        		}
			}
        }
        try {
			getSqlSessionFactoryBean().buildSqlSessionFactory(configuration);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	@SuppressWarnings("unchecked")
	public void clearMappedStatements(Class<?> classConfig, Configuration configuration, String resource) throws Exception {
    	System.out.println("清除mappedStatements");
        Field field = classConfig.getDeclaredField("mappedStatements");
        field.setAccessible(true);
        Map<String, ?> map = (Map<String, ?>)field.get(configuration);
        //手动解析mapper文件，把所有要移除的节点 ID 查出来
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
				//有可能使用了 <cache/>标签，没有 id 的情况下会报错
				if (defaultElement.attribute("id") != null) {
					System.out.println("移除id：" + namespace + "." + defaultElement.attribute("id").getValue());
					map.remove(namespace + "." + defaultElement.attribute("id").getValue());
					System.out.println("移除id：" + defaultElement.attribute("id").getValue());
					map.remove(defaultElement.attribute("id").getValue());
				}
			}
		}
        field.set(configuration, map);
    }
	
    private void removeConfig(Configuration configuration, String resource) throws Exception {
        Class<?> classConfig = configuration.getClass();
        clearMappedStatements(classConfig, configuration, resource);
        clearSet(classConfig, configuration, "loadedResources", resource);
        clearMap(classConfig, configuration, "caches");
        clearMap(classConfig, configuration, "resultMaps");
        clearMap(classConfig, configuration, "parameterMaps");
        clearMap(classConfig, configuration, "keyGenerators");
        clearMap(classConfig, configuration, "sqlFragments");
    }
    
    private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName, String resource) throws Exception {
    	System.out.println("清除loadedResources");
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Set<?> set = (Set<?>) field.get(configuration);
        set.remove(resource);
    }
    
	private static void clearMap(Class<?> classConfig, Configuration configuration, String fieldName) throws Exception {
		Field field = classConfig.getDeclaredField(fieldName);
		field.setAccessible(true);
		Map<?, ?> mapConfig = (Map<?, ?>) field.get(configuration);
		if(mapConfig != null && mapConfig.size() > 0) {
			mapConfig.clear();
		}
	}
    
    /**
	 * @return the sqlSessionFactoryBean
	 */
	public SqlSessionFactoryBean getSqlSessionFactoryBean() {
		Ioc ioc = Mvcs.getIoc();
		if (ioc == null) {
			NutMvcContext context = Mvcs.ctx();
			Set<String> set = context.iocs.keySet();
			for (String key : set) {
				ioc = context.iocs.get(key);//就一个而已
			}
		}
		if (ioc != null) {
			return ioc.get(SqlSessionFactoryBean.class);
		}else {
			System.out.println("\n\n\n\n\n\n\n ===== ioc 为空啊！我也不知道怎么办啊~~");
		}
		return null;
	}
    
}
