package cn.impler.framework.mybatis.dao.dialect;

public class OracleDialect implements DBDialect {

	@Override
	public String getPaginationSql(String origSql) {
		
		StringBuffer sb = new StringBuffer();
		// first '?' : limit
		// second '?' : offset
		sb.append("SELECT * FROM (SELECT ROWNUM AS RN, TEMP.* FROM (")
				.append(origSql)
				.append(") TEMP WHERE ROWNUM <= ?) WHERE RN >= ? + 1");
		return sb.toString();
	}

	@Override
	public boolean isOffsetFront() {
		return false;
	}

	@Override
	public boolean isUseLimitAsEnd() {
		return false;
	}

}
