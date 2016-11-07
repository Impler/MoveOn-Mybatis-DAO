package cn.impler.framework.mybatis.dao.dialect;

public interface DBDialect {

	String getLimitSql(String origSql);
}
