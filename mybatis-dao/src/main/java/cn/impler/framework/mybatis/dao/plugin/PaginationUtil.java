package cn.impler.framework.mybatis.dao.plugin;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import cn.impler.framework.mybatis.dao.dto.Pagination;

/**
 * a utility class with multi-conveninent pagination query methods
 * @author impler
 *
 */
public class PaginationUtil {

	public static <E> List<E> selectByPagination(SqlSession session, String pageStmtId, Object parameter, Pagination page){
		assert null != page;
		assert page.getCurrentPage() >= 1;
		assert page.getPageSize() > 0;
		int offset = page.getOffset();
		int limit = page.getPageSize();
		RowBounds rb = new RowBounds(offset, limit);
		return session.selectList(pageStmtId, parameter, rb);
	}
	
	public static <E> List<E> selectByPagination(SqlSession session, String pageStmtId, String countStmtId, Object parameter, Pagination page){
		assert null != page;
		if(null != countStmtId){
			int count = (Integer) session.selectOne(countStmtId, parameter);
			page.setCount(count);
		}
		return selectByPagination(session, pageStmtId, parameter, page);
	}

	public static <E> List<E> selectByPagination(SqlSession session, String pageStmtId, Object parameter, RowBounds rb){
		return session.selectList(pageStmtId, parameter, rb);
	}
	
}
