package cn.wizzer.app.wb.modules.common.nutzMybatis.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月11日 上午10:34:53
 * 数据映射类，如果返回的数据类型不想映射到 model 的话可以映射到这个类，这个类没有对数据进行大小写转换
 */
public class DataRecord extends HashMap<String, Object> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Log log = Logs.get();

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
		return super.get(key);
    }
    
    public String getString(String key) {
    	try{
    		Object val = get(key);
    		if(null == val) return null;
    		return val.toString();
    	}catch(ClassCastException e) {
    		e.printStackTrace();
    		log.error("数据转换异常 【key：" + key + ", value：" + get(key) + " 】", e);
    		return null;
    	}
    }
    
    public Boolean getBoolean(String key) {
    	try{
    		Object value = get(key);
        	if(value instanceof Boolean) {
    			return ((Boolean)value).booleanValue();
    		}else {
    			return Boolean.valueOf(value.toString());
    		}
    	}catch(ClassCastException e) {
    		e.printStackTrace();
    		log.error("数据转换异常 【key：" + key + ", value：" + get(key) + " 】", e);
    		return null;
    	}
    }
    
    public Long getLong(String key) {
    	try{
    		Object value = get(key);
        	if(value instanceof BigDecimal)  return ((BigDecimal)value).longValue();
    			
    		if(value instanceof Long) return (Long)value;
    			
    		return Long.valueOf(value.toString());
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error("数据转换异常 【key：" + key + ", value：" + get(key) + " 】", e);
    		return null;
    	}
    }
    
    public Integer getInteger(String key) {
    	try{
    		Object value = get(key);
    		if(value instanceof BigDecimal) return ((BigDecimal)value).intValue();
    			
    		if(value instanceof Long) return ((Long)value).intValue();
    			
    		if(value instanceof Integer) return (Integer)value;
    			
    		return Integer.valueOf(value.toString());
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error("数据转换异常 【key：" + key + ", value：" + get(key) + " 】", e);
    		return null;
    	}
    }
    
    public Double getDouble(String key) {
    	try {
    		Object value = get(key);
    		if(value instanceof BigDecimal) return ((BigDecimal)value).doubleValue();
    		
    		if(value instanceof Double) return (Double)value;
    	
    		return Double.valueOf(value.toString());
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    		log.error("数据转换异常 【key：" + key + ", value：" + get(key) + " 】", e);
    		return null;
    	}
    }
    
    public Date getDate(String key) {
    	try{
    		return (Date)(get(key));
    	}catch(ClassCastException e) {
    		e.printStackTrace();
    		log.error("数据转换异常 【key：" + key + ", value：" + get(key) + " 】", e);
    		return null;
    	}
    }
    
    /**
     * 根据 key 和 日期格式返回一个格式化后的字符串
     * @param key 数据 key
     * @param format 要格式化的日期格式
     * @return
     */
    public String getDateString(String key, String format) {
    	Date value = getDate(key);
    	if(value == null) return null;
    	
    	if(format != null && !"".equals(format)) return getDateString(key);
    	
    	try{
        	return new SimpleDateFormat(format).format(value);
    	}catch(Exception e){
    		log.error("数据转换异常 【key：" + key + ", value：" + get(key) + " 】", e);
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
    
    public boolean isEmpty(String key) {
    	try{
    		Object value = get(key);
    		return value == null || value.toString().equals("");
    	}catch(ClassCastException e) {
    		e.printStackTrace();
    		log.error("数据转换异常 【key：" + key + ", value：" + get(key) + " 】", e);
    		return false;
    	}
    }
    
}
