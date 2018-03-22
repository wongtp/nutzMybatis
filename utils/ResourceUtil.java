/**
 * 
 */
package cn.wizzer.app.wb.modules.common.nutzMybatis.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月11日 下午3:09:24
 */
public class ResourceUtil {
	
	/**
	 * 获取所有 mapper 文件
	 * @return
	 */
	public static String[] getMappers() {
		List<String> pathList = new ArrayList<>();
		getMappers(Paths.get(getClassPath()), pathList, getClassPath().length());
		return pathList.toArray(new String[0]);
	}
	
	/**
	 * 获取所有 mapper 文件的路径
	 * @return
	 */
	public static List<String> getMapperDirs() {
		List<String> pathList = new ArrayList<>();
		getMapperDirs(Paths.get(getClassPath()), pathList);
		return pathList;
	}
	
	/**
	 * 递归出来所有 mapper 文件的路径
	 * @param path
	 * @param pathList
	 */
	private static void getMapperDirs(Path path, List<String> pathList) {
		if (!path.toFile().isDirectory()) {
			String realPath = path.toAbsolutePath().toString();
			if (realPath.endsWith("-mapper.xml")) {
				pathList.add(path.getParent().toString());
			}
			return;
		}
		DirectoryStream<Path> directoryStream = null;
		try {
			directoryStream = Files.newDirectoryStream(path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(Path item : directoryStream) {
			getMapperDirs(item, pathList);
        }
	}
	
	/**
	 * 递归出所有 mapper 文件
	 * @param path
	 * @param pathList
	 * @param classPathLength
	 */
	private static void getMappers(Path path, List<String> pathList, int classPathLength) {
		if (!path.toFile().isDirectory()) {
			String realPath = path.toAbsolutePath().toString();
			if (realPath.endsWith("-mapper.xml")) {
				pathList.add(realPath.substring(classPathLength));
			}
			return;
		}
		DirectoryStream<Path> directoryStream = null;
		try {
			directoryStream = Files.newDirectoryStream(path);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(Path item : directoryStream) {
			getMappers(item, pathList, classPathLength);
        }
	}
	
	/**
	 * 获取 ClassPath 路径
	 * @return
	 */
	public static String getClassPath() {
		URL rootPath = Thread.currentThread().getContextClassLoader().getResource("/");
		String classPath = null;
		try {
			if (rootPath != null) {
				classPath = rootPath.toURI().getPath();
			}else {
				classPath = ResourceUtil.class.getResource("/").toURI().getPath();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (classPath.indexOf("/") == 0) {
			classPath = classPath.substring(1);
		}
		return classPath;
	}
	
}
