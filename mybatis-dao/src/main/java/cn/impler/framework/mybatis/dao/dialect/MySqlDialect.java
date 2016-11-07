package cn.impler.framework.mybatis.dao.dialect;

public class MySqlDialect implements DBDialect {

	@Override
	public String getLimitSql(String origSql) {
		return null;
	}

}
