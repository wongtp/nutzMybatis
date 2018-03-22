package cn.wizzer.app.wb.modules.common.nutzMybatis.dto;

import java.io.Serializable;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月11日 上午10:34:53
 * 数据映射类，如果返回的数据类型不想映射到 model 的话可以映射到这个类，这个类的 key 都是大写
 */
public class DataRecordUp extends DataRecord implements Serializable {
	
	private static final long serialVersionUID = 1L;
    
	@Override
    public Object put(String key, Object value) {
    	return super.put(key.toUpperCase(), value);
    }
}
