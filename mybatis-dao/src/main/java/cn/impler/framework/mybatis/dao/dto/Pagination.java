package cn.impler.framework.mybatis.dao.dto;

import java.io.Serializable;

public class Pagination implements Serializable {

	private static final long serialVersionUID = -8867570785672936072L;

	private int pageSize;
	private int currentPage;
	private long count;
	private int pages;

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

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	@Override
	public String toString() {
		return "Pagination [pageSize=" + pageSize + ", currentPage="
				+ currentPage + ", count=" + count + ", pages=" + pages + "]";
	}
	
}
