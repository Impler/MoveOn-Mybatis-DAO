package cn.impler.framework.mybatis.dao.plugin;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
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
import cn.impler.framework.mybatis.dao.constant.DaoConstant;
import cn.impler.framework.mybatis.dao.dialect.DBDialect;
import cn.impler.framework.mybatis.dao.dialect.MySqlDialect;
import cn.impler.framework.mybatis.dao.dialect.OracleDialect;
import cn.impler.framework.mybatis.dao.util.StringUtils;

/**
 * 
 * @author impler
 *
 */
@Intercepts(@Signature(args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }, method = "query", type = Executor.class))
public class PaginationPlugin implements Interceptor {

	private static Logger logger = LoggerFactory.getLogger(PaginationPlugin.class);

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
		
		// if it is not a paging query, execute normally
		if (RowBounds.DEFAULT.equals(rowBounds) || (rowBounds.getOffset() == RowBounds.NO_ROW_OFFSET
				&& rowBounds.getLimit() == RowBounds.NO_ROW_OFFSET)) {
			logger.debug("not paging query, execute normally");
			return invocation.proceed();
		}
		
		// otherwise, it needs to rebuild the MappedStatement object to support paging query on the database level
		
		MappedStatement origMs = (MappedStatement) args[0];
		Configuration config = origMs.getConfiguration();
		
		
		// in order to avoid influence each other, create a new MappedStatement instance with a given suffix id
		String paginationMsId = origMs.getId() + DaoConstant.V_PAGE_MS_ID_SUFFIX;
		
		Object origParameter = args[1];

		if(!config.hasStatement(paginationMsId)){
			final BoundSql boundSql = newBoundSql(config, rowBounds, origMs, origParameter);
			SqlSource sqlSource = new SqlSource() {
				@Override
				public BoundSql getBoundSql(Object parameterObject) {
					return boundSql;
				}
			};
			recreateMappedStatement(origMs, sqlSource);
		}else{
			BoundSql boundSql = config.getMappedStatement(paginationMsId).getBoundSql(origParameter);
			boundSql.setAdditionalParameter(DaoConstant.V_SQL_OFFSET, rowBounds.getOffset());
			boundSql.setAdditionalParameter(DaoConstant.V_SQL_LIMIT, getLimit(rowBounds));
		}
		
		// reset arguments
		args[0] = config.getMappedStatement(paginationMsId);
		
		// avoid Mybatis build-in pagination
		args[2] = RowBounds.DEFAULT;
		return invocation.proceed();
	}

	
	private BoundSql newBoundSql(Configuration config, RowBounds rowBounds,
			MappedStatement ms, Object origParameter) throws Exception {
		
		BoundSql origBoundSql = ms.getBoundSql(origParameter);
		String sql = origBoundSql.getSql();
		processDBDialect(ms.getConfiguration().getEnvironment());
		// 加入分页的sql
		sql = dialect.getPaginationSql(sql);
		
		List<ParameterMapping> paraMappings = newPaginationParameterMapping(
				config, origBoundSql.getParameterMappings());
		
		BoundSql boundSql = new BoundSql(config, sql, paraMappings, origParameter);
		boundSql.setAdditionalParameter(DaoConstant.V_SQL_OFFSET, rowBounds.getOffset());
		boundSql.setAdditionalParameter(DaoConstant.V_SQL_LIMIT, getLimit(rowBounds));
		return boundSql;
	}

	/**
	 * the bounds of pagination offset and limit depend on the specific database
	 * @param rb
	 * @return
	 */
	private int getLimit(RowBounds rb){
		if(this.dialect.isUseLimitAsEnd()){
			return rb.getLimit();
		}else{
			return rb.getOffset() + rb.getLimit();
		}
	}
	
	/**
	 * add  pagination parameter, limit and offset
	 * @param config
	 * @param origParaMappings
	 * @return
	 */
	private List<ParameterMapping> newPaginationParameterMapping(
			Configuration config, List<ParameterMapping> origParaMappings) {
		
		List<ParameterMapping> paraMappings = new ArrayList<ParameterMapping>(origParaMappings);
		String front = null;
		String back = null;
		
		if (dialect.isOffsetFront()) {
			front = DaoConstant.V_SQL_OFFSET;
			back = DaoConstant.V_SQL_LIMIT;
		} else {
			front = DaoConstant.V_SQL_LIMIT;
			back = DaoConstant.V_SQL_OFFSET;
		}
		// add pagination parameter
		paraMappings.add(new ParameterMapping.Builder(config, front, Integer.class).build());
		paraMappings.add(new ParameterMapping.Builder(config, back, Integer.class).build());
		return paraMappings;
	}

	/**
	 * recreate a new MappedStatement instance and add it to Configuration
	 * most of properties of the new MappedStatement object copy from original MappedStatement object, beside SqlSource
	 * @param origMs
	 * @param newSqlSource
	 */
	private void recreateMappedStatement(MappedStatement origMs, SqlSource newSqlSource) {
		Configuration config = origMs.getConfiguration();
		SqlCommandType sqlCommandType = origMs.getSqlCommandType();
		// make a new statement id for pagination
		String id = origMs.getId() + DaoConstant.V_PAGE_MS_ID_SUFFIX;
		MappedStatement.Builder builder = new MappedStatement.Builder(config,
				id, newSqlSource, sqlCommandType)
				.resource(origMs.getResource())
				.fetchSize(origMs.getFetchSize()).timeout(origMs.getTimeout())
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
		if (null != this.dialect) {
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

	/**
	 * create a proxy object
	 * @param target a Executor instance here
	 */
	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// Not Implemented
	}

}