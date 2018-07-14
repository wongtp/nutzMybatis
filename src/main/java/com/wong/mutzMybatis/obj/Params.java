package com.wong.mutzMybatis.obj;

import java.util.HashMap;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年3月27日 下午12:52:24
 * 为了方便传递参数，为了世界的和平，贯彻爱与真实的邪恶，白洞白色的明天等着我们，就是这样，喵~~
 */
public class Params extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	
	private Params(String key, Object value) {
		super.put(key, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getObj(String key) {
		T t = null;
		try {
			t = (T) this.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
	
	public static Params NEW(String key, Object value) {
		return new Params(key, value);
	}
	
	public Params put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}
