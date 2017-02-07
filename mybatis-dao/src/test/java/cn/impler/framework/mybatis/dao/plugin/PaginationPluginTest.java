package cn.impler.framework.mybatis.dao.plugin;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import cn.impler.framework.mybatis.dao.domain.App;
import cn.impler.framework.mybatis.dao.domain.User;
import cn.impler.framework.mybatis.dao.dto.Pagination;
import cn.impler.framework.mybatis.dao.util.SqlSessionUtil;

public class PaginationPluginTest {

	// TEST MYSQL
	@Test
	public void testQueryUsersWithNoParams() {
		SqlSession session = SqlSessionUtil.getSession();
		Pagination page = new Pagination(5, 1);
		List<User> users = PaginationUtil.selectByPagination(session,
				"user.queryUsersWithNoParams", null, page);
	
		for(User u: users){
			System.out.println(u);
		}
		
		page.nextPage();
		
		users = PaginationUtil.selectByPagination(session,
				"user.queryUsersWithNoParams", null, page);
		
		for(User u: users){
			System.out.println(u);
		}
	}
	
	@Test
	public void queryUsersWithParams() {
		SqlSession session = SqlSessionUtil.getSession();
		Pagination page = new Pagination(5, 1);
		User user = new User();
		List<User> users = PaginationUtil.selectByPagination(session,
				"user.queryUsersWithParams", user, page);
	
		for(User u: users){
			System.out.println(u);
		}
		
		page.nextPage();
		user.setUsername("KOBE");
		users = PaginationUtil.selectByPagination(session,
				"user.queryUsersWithParams", user, page);
		
		for(User u: users){
			System.out.println(u);
		}
	}
	
	
	
	// TEST ORACLE
	@Test
	public void testQueryAppsWithNoParams() {
		SqlSession session = SqlSessionUtil.getSession();
		Pagination page = new Pagination(5, 1);
		List<App> apps = PaginationUtil.selectByPagination(session,
				"app.queryAppsWithNoParams", null, page);
		
		for(App u: apps){
			System.out.println(u);
		}
		
		page.nextPage();
		
		apps = PaginationUtil.selectByPagination(session,
				"app.queryAppsWithNoParams", null, page);
		
		for(App u: apps){
			System.out.println(u);
		}
		
	}

}
