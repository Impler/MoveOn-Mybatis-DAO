package cn.impler.framework.mybatis.dao.dialect;

public class MySqlDialect implements DBDialect {

	@Override
	public String getPaginationSql(String origSql) {
		// first '?' : offset
		// second '?' : limit
		return new StringBuffer(origSql).append(" LIMIT ?, ?").toString();
	}

	@Override
	public boolean isOffsetFront() {
		return true;
	}

	@Override
	public boolean isUseLimitAsEnd() {
		return true;
	}

	

}
