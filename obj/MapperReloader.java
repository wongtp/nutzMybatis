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
	private static WatchService watcher = null;
	private static WatchKey key = null;
	private static boolean isWatcher = true;
	
    @Override  
    public void contextInitialized(ServletContextEvent sce) {
    	//判断在 debug 模式下才做重新加载 mapper 文件的操作
    	if (isDebug()) {
    		isWatcher = true;
    		//新开一条线程，而不至于阻塞
    		Thread thread = new Thread(new MapperReloader());
    		thread.start();
    		sce.getServletContext().setAttribute("currentThread", thread);
		}
    }
    
    /**
     * 如果不写的话貌似在 tomcat 自动重启的时候会销毁不了而报错
     */
    @Override  
    public void contextDestroyed(ServletContextEvent sce) {
    	isWatcher = false;
    	if(key != null ) {
    		key.reset();
    		key.cancel();
    	}
    	if(watcher != null ) {
    		try {
    			watcher.poll();
    			watcher.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	Thread thread = (Thread) sce.getServletContext().getAttribute("currentThread");
    	if (thread != null) {
    		thread.interrupt();
		}
    }
    
	@Override
	public void run() {
		//获取所有以 -mapper.xml 结尾文件的目录
		List<String> mapperDirs = ResourceUtil.getMapperDirs();
		if (mapperDirs != null && mapperDirs.size() > 0) {
			try {
				//开启一个文件监视器
				watcher = FileSystems.getDefault().newWatchService();
				//把所有要监控的目录都注册进来
				for (String dir : mapperDirs) {
					System.out.println("监控mapper目录：" + dir);
					Paths.get(dir).register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
			long formerly = 0;
			while (true) {  
				try {
					key = watcher.take();
	            } catch (InterruptedException x) {
	                return;
	            }
				for (WatchEvent<?> event: key.pollEvents()) {  
	                long current = System.currentTimeMillis();
	                //10秒内防止重复加载 mapper 文件，因为这个监视器会跳出来两次文件被修改的信号； 同样这些目录都是只监控文件的修改就行了，创建和删除好像没必要
	                if (current - formerly > 10000 && "ENTRY_MODIFY".equals(event.kind().toString())) {
	                	try {
	                		reloadXML(event.context().toString());
	                	} catch (Exception e1) {
	                		e1.printStackTrace();
	                	}  
	                	formerly = current;
	                }
	            }
				if (!key.reset() || !isWatcher) {  
					break;  
				}  
			} 
		}
	}
	
	/**
	 * 判断是否在 debug 状态，如果不这样写的话可以另外写个配置文件
	 * @return
	 */
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
	
	public void reloadXML(String fileName) {
        Configuration configuration = getSqlSessionFactory().getConfiguration();
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
        			System.out.println("Failed to parse mapping resource: '" + resource + "'");
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
				System.out.println("移除id：" + namespace + "." + defaultElement.attribute("id").getValue());
				map.remove(namespace + "." + defaultElement.attribute("id").getValue());
				System.out.println("移除id：" + defaultElement.attribute("id").getValue());
				map.remove(defaultElement.attribute("id").getValue());
			}
		}
        field.set(configuration, map);
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
    
    private void clearSet(Class<?> classConfig, Configuration configuration, String fieldName, String resource) throws Exception {
    	System.out.println("清除loadedResources");
        Field field = classConfig.getDeclaredField(fieldName);
        field.setAccessible(true);
        Set<?> set = (Set<?>) field.get(configuration);
        set.remove(resource);
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
