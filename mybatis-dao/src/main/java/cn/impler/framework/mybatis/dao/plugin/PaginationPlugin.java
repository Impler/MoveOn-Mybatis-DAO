package cn.impler.framework.mybatis.dao.plugin;

import java.sql.DatabaseMetaData;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMap;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.impler.framework.mybatis.dao.constant.DBType;
import cn.impler.framework.mybatis.dao.dialect.DBDialect;
import cn.impler.framework.mybatis.dao.dialect.MySqlDialect;
import cn.impler.framework.mybatis.dao.dialect.OracleDialect;
import cn.impler.framework.mybatis.dao.util.StringUtils;

@Intercepts(@Signature(args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }, method = "query", type = Executor.class))
public class PaginationPlugin implements Interceptor {

	private static Logger logger = LoggerFactory
			.getLogger(PaginationPlugin.class);

	private DBDialect dialect;

	public DBDialect getDialect() {
		return dialect;
	}

	public void setDialect(DBDialect dialect) {
		this.dialect = dialect;
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		RowBounds rowBounds = (RowBounds) args[2];
		if (rowBounds.getOffset() == RowBounds.NO_ROW_OFFSET
				&& rowBounds.getLimit() == RowBounds.NO_ROW_OFFSET) {
			logger.debug("not paging query, execute normally");
			return invocation.proceed();
		}

		MappedStatement ms = (MappedStatement) args[0];
		// original parameter
		Object origParameter = args[1];

		String sql = ms.getBoundSql(origParameter).getSql();

		processDBDialect(ms.getConfiguration().getEnvironment());

		sql = dialect.getLimitSql(sql);

		recreateMappedStatement(ms);

		return invocation.proceed();
	}

	private void recreateMappedStatement(MappedStatement origMs) {
		Configuration config = origMs.getConfiguration();
		SqlCommandType sqlCommandType = origMs.getSqlCommandType();
		String id = origMs.getId() + "-pagination";
		SqlSource sqlSource = origMs.getSqlSource();
		MappedStatement.Builder builder = new MappedStatement.Builder(config,
				id, sqlSource, sqlCommandType)
				.resource(origMs.getResource())
				.fetchSize(origMs.getFetchSize())
				.timeout(origMs.getTimeout())
				.statementType(origMs.getStatementType())
				.keyGenerator(origMs.getKeyGenerator())
				.keyProperty(StringUtils.join(origMs.getKeyProperties(), ","))
				.keyColumn(StringUtils.join(origMs.getKeyColumns(), ","))
				.databaseId(origMs.getDatabaseId()).lang(origMs.getLang())
				.resultOrdered(origMs.isResultOrdered())
				.resulSets(StringUtils.join(origMs.getResulSets(), ","))
				.resultMaps(origMs.getResultMaps())
				.resultSetType(origMs.getResultSetType())
				.flushCacheRequired(origMs.isFlushCacheRequired())
				.useCache(origMs.isUseCache()).cache(origMs.getCache());

		ParameterMap origParaMap = origMs.getParameterMap();
		List<ParameterMapping> paraMappings = origParaMap.getParameterMappings();
		ParameterMap paraMap = new ParameterMap.Builder(config, id, origParaMap.getClass(), paraMappings).build();
		builder.parameterMap(paraMap);
		MappedStatement statement = builder.build();
		config.addMappedStatement(statement);
	}

	/**
	 * If DBDialect instance has not initialized, a new one will be created
	 * according to the specific database type.
	 * 
	 * @param environment
	 * @throws Exception
	 */
	private void processDBDialect(Environment environment) throws Exception {
		if (null == this.dialect) {
			return;
		} else {
			logger.debug("a new DBDialect instance will be created according to the specific database type");
			DatabaseMetaData dbMetaData = environment.getDataSource()
					.getConnection().getMetaData();
			String prdName = dbMetaData.getDatabaseProductName();
			DBType dbType = DBType.valueOf(prdName.toUpperCase());
			switch (dbType) {
			case MYSQL:
				this.dialect = new MySqlDialect();
				break;
			case ORACLE:
				this.dialect = new OracleDialect();
				break;
			default:
				// TODO 待确定最终的异常
				throw new Exception("No valid database dialect found");
			}
		}
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// Not Implemented
	}

}
