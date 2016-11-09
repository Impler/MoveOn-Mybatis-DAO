package cn.impler.framework.mybatis.dao.dialect;

/**
 * different databases may have different features.
 * this interface aim to abstract those differences
 * @author impler
 *
 */
public interface DBDialect {

	/**
	 * build paging sql
	 * @param origSql
	 * @return
	 */
	String getPaginationSql(String origSql);

	/**
	 * does the index of offset parameter, in the new pagination sql string, is
	 * in the front of the limit parameter?
	 * @return
	 */
	boolean isOffsetFront();
	
	/**
	 * does use the limit value of RowBounds as end page number 
	 * @return
	 */
	boolean isUseLimitAsEnd();
}
