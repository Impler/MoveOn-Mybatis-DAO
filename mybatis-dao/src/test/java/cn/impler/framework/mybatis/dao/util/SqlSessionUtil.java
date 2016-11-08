package cn.impler.framework.mybatis.dao.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlSessionUtil {

	private static SqlSessionFactory factory = null;

	private SqlSessionUtil() {
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		if (null == factory) {
			synchronized (SqlSessionUtil.class) {
				if (null == factory) {
					InputStream input;
					try {
						input = Resources.getResourceAsStream("mybatis.xml");
						factory = new SqlSessionFactoryBuilder().build(input);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return factory;
	}

	
	public static SqlSession getSession(){
		return getSqlSessionFactory().openSession(true);
	}
}
