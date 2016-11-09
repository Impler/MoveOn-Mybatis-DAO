package cn.impler.framework.mybatis.dao.dto;

import java.io.Serializable;

public class Pagination implements Serializable {

	private static final long serialVersionUID = -8867570785672936072L;
	
	// record size per page 
	private int pageSize;
	
	// current page number, starts at 1
	private int currentPage;
	
	// total record count
	private int count;
	
	public Pagination() {
		super();
	}

	public Pagination(int pageSize) {
		this();
		this.pageSize = pageSize;
	}

	public Pagination(int pageSize, int currentPage) {
		this();
		this.pageSize = pageSize;
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * next page number
	 */
	public void nextPage(){
		this.currentPage ++;
	}
	
	/**
	 * the count of all pages
	 * @return
	 */
	public int getPages() {
		if(count % pageSize == 0){
			return count / pageSize;
		}else{
			return count / pageSize + 1;
		}
	}

	public int getOffset() {
		return (getCurrentPage() - 1) * this.pageSize;
	}
	
	@Override
	public String toString() {
		return "Pagination [pageSize=" + pageSize + ", currentPage="
				+ currentPage + ", count=" + count + "]";
	}
}
