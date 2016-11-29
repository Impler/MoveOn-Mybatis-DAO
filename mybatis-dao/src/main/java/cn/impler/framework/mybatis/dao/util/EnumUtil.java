package cn.impler.framework.mybatis.dao.util;

import cn.impler.framework.mybatis.dao.dto.IGenericEnum;

/**
 * Utility class of Enum
 * @author impler
 * @date 2016-11-29
 */
public class EnumUtil {
	
	/**
	 * get the IGenericEnum object by enum id
	 * @param type enum class
	 * @param key enum id
	 * @return
	 */
	public static <E extends IGenericEnum> E getGenericEnumConstant(Class<E> type, int key){
		E[] constants = type.getEnumConstants();
		if(null == constants){
			return null;
		}
		for(E c : constants){
			if(c.getId() == key){
				return c;
			}
		}
		return null;
	}
	
	/**
	 * get the Enum object by enum id
	 * @param type enum class
	 * @param key enum id
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <E extends Enum> Enum getEnumConstant(Class<E> type, int key){
		E[] constants = type.getEnumConstants();
		if(null == constants){
			return null;
		}
		if(IGenericEnum.class.isAssignableFrom(type)){
			for(E c : constants){
				if(((IGenericEnum)c).getId() == key){
					return c;
				}
			}
		}
		return null;
	}
}
