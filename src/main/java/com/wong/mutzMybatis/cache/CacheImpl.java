package com.wong.mutzMybatis.cache;

import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年7月13日 下午10:35:00
 */
public class CacheImpl implements Cache {

	@Override
	public String getId() {
		return null;
	}

	@Override
	public void putObject(Object key, Object value) {
	}

	@Override
	public Object getObject(Object key) {
		return null;
	}

	@Override
	public Object removeObject(Object key) {
		return null;
	}

	@Override
	public void clear() {
	}

	@Override
	public int getSize() {
		return 0;
	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return null;
	}

}
