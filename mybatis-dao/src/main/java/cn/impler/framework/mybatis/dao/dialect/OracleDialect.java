package cn.impler.framework.mybatis.dao.dialect;

public class OracleDialect implements DBDialect {

	@Override
	public String getLimitSql(String origSql) {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT * FROM (SELECT ROWNUM AS RN, TEMP.* FROM (")
				.append(origSql)
				.append(") TEMP WHERE ROWNUM < ?) WHERE RN >= ?");
		return sb.toString();
	}

}
