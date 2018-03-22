package cn.wizzer.app.wb.modules.common.nutzMybatis.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月11日 上午10:34:53
 * 数据映射类，如果返回的数据类型不想映射到 model 的话可以映射到这个类，这个类没有对数据进行大小写转换
 */
public class DataRecord extends HashMap<String, Object> implements Serializable {
	
	private static final Logger logger = LoggerFactory.getLogger(DataRecord.class);
	private static final long serialVersionUID = 1L;

	public DataRecord() {}

	/**
	 * 注意！！ 使用该方法时注意传参的方式，第一个为 key1，第二个为 value1，第三个为 key2，第四个为 value2 ......以此类推<br>
	 * key 值必须为 String 类型，value 随便<br>
	 * @param args
	 */
	public DataRecord(Object... args) {
		put(args);
	}

    public Object get(String key) {
		return super.get(((String)key));
    }
    
    public String getString(String key) {
    	try{
    		Object value = get(key);
    		if(value != null) {
    			return value.toString();
    		}else {
    			return null;
    		}
    	}catch(ClassCastException e) {
    		logger.warn("数据转换异常【key：" + key + ", value：" + get(key) + "】", e);
    		return null;
    	}
    }
    
    public Long getLong(String key) {
    	try{
    		Object value = get(key);
    		Long val = Long.valueOf(value.toString());
    		return val;
    	}catch(Exception e){
    		logger.warn("数据转换异常【key：" + key + ", value：" + get(key) + "】", e);
    		return null;
    	}
    }
    
    public Integer getInteger(String key) {
    	try{
    		Object value = get(key);
    		Integer val = Integer.valueOf(value.toString());
    		return val;
    	}catch(Exception e) {
    		logger.warn("数据转换异常【key：" + key + ", value：" + get(key) + "】", e);
    		return null;
    	}
    }
    
    public Double getDouble(String key) {
    	try {
    		BigDecimal value= ((BigDecimal)(get(key)));
    		return value == null?null:value.doubleValue();
    	}catch(Exception e) {
    		logger.warn("数据转换异常【key：" + key + ", value：" + get(key) + "】", e);
    		return null;
    	}
    }
    
    public Date getDate(String key) {
    	try{
    		return (Date)(get(key));
    	}catch(ClassCastException e) {
    		logger.warn("数据转换异常【key：" + key + ", value：" + get(key) + "】", e);
    		return null;
    	}
    }
    
    /**
     * 根据 key 和 日期格式返回一个格式化后的字符串
     * @param key
     * @param format
     * @return
     */
    public String getDateString(String key, String format) {
    	Date value = getDate(key);;
    	if(value == null) {
    		return null;
    	}else if(format != null && !"".equals(format)) {
    		return getDateString(key);
    	}
    	try{
    		DateFormat sdf = new SimpleDateFormat(format);
        	return sdf.format(value);
    	}catch(Exception e){
    		logger.warn("数据转换异常【key：" + key + ", value：" + get(key) + "】", e);
    		return null;
    	}
    }
    
    public String getDateString(String key) {
    	return getDateString(key, "yyyy-MM-dd hh:mm");
    }

	/**
	 * 注意！！ 使用该方法时注意传参的方式，第一个为 key1，第二个为 value1，第三个为 key2，第四个为 value2 ......以此类推<br>
	 * key 值必须为 String 类型，value 随便<br>
	 * @param args
	 */
	public void put(Object... args) {
		for (int i = 1; i < args.length; i += 2) {
			put(String.valueOf(args[i - 1]), args[i]);
		}
	}

    public Object put(String key, Object value) {
    	return super.put(key, value);
    }
    
}
