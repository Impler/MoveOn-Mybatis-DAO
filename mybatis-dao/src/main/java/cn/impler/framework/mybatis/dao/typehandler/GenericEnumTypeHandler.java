package cn.impler.framework.mybatis.dao.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import cn.impler.framework.mybatis.dao.dto.IGenericEnum;
import cn.impler.framework.mybatis.dao.util.EnumUtil;

/**
 * GenericEnum TypeHandler
 * @author impler
 * @date 2016-11-29
 * @param <E> specified IGenericEnum type
 */
public class GenericEnumTypeHandler<E extends IGenericEnum> extends BaseTypeHandler<E> {
	
	private Class<E> type;

	public GenericEnumTypeHandler() {
		super();
	}

	@SuppressWarnings("unchecked")
	public GenericEnumTypeHandler(String typeClass) throws Exception{
		try {
			Class<?> clazz = Class.forName(typeClass);
			if(IGenericEnum.class.isAssignableFrom(clazz)){
				this.type = (Class<E>) clazz;
			}else{
				throw new IllegalArgumentException(clazz + " is not a IGenericEnum class");
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public GenericEnumTypeHandler(Class<E> type) {
		this();
		if (type == null) {
			throw new IllegalArgumentException("Type argument cannot be null");
		}
		this.type = type;
	}

	public Class<E> getType() {
		return type;
	}

	public void setType(Class<E> type) {
		this.type = type;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter,
			JdbcType jdbcType) throws SQLException {
		ps.setInt(i, parameter.getId());
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		if (rs.wasNull()) {
			return null;
		} else {
			int key = rs.getInt(columnName);
			return EnumUtil.getGenericEnumConstant(type, key);
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		if (rs.wasNull()) {
			return null;
		} else {
			int key = rs.getInt(columnIndex);
			return EnumUtil.getGenericEnumConstant(type, key);
		}
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		if (cs.wasNull()) {
			return null;
		} else {
			int key = cs.getInt(columnIndex);
			return EnumUtil.getGenericEnumConstant(type, key);
		}
	}

}
