/**
 * 
 */
package cn.wizzer.app.wb.modules.common.nutzMybatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import cn.wizzer.app.wb.modules.common.nutzMybatis.config.SqlSessionFactoryBean;
import cn.wizzer.app.wb.modules.common.nutzMybatis.dto.DataRecord;

/**
 * @author 黄小天 wongtp@outlook.com
 * @date 2018年2月10日 下午8:27:07
 */
public class Tester {
	//
	//测试是否成功连接到数据库平查询到数据
	public static void main(String[] args) {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		SqlSessionFactory sqlSessionFactory = null;
	    SqlSession session = null;
		try {
			//String resource = "mybatis-config.xml";
	        //InputStream inputStream = Resources.getResourceAsStream(resource);
	        //sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			
	        sqlSessionFactory = bean.getSqlSessionFactory();
	        session = sqlSessionFactory.openSession(true);
	        DataRecord result = session.selectOne("org.mybatis.example.BlogMapper.selectBlog");
			System.out.println("Result: " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
