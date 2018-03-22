/**
 * 
 */
package cn.wizzer.app.wb.modules.common.nutzMybatis.obj;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月25日 下午7:14:00
 */
public class IgnoreDTDEntityResolver implements EntityResolver {  
	  
	 @Override  
	 public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {  
		 return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
	 }  
}