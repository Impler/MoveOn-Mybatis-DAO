package cn.impler.framework.mybatis.dao.intf;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.apache.ibatis.session.SqlSession;

/**
 * the abstract of the BaseDao 
 * @author impler
 * @date 2016-11-29
 * @param <T> specified dao class
 * @param <E> entity class
 * @param <K> ID class
 */
public abstract class AbstractDao<T extends BaseDao<E, K>, E, K> implements BaseDao<E, K> {

	//dao class object
	protected Class<T> daoClass;
	
	@Resource(name="sqlSession")
	private SqlSession sqlSession;
	
	@SuppressWarnings("unchecked")
	public AbstractDao() {
		// get the generic class object by reflection
		daoClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		Assert.assertNotNull("daoClass can not be null", daoClass);
	}
	
	public T getDao(){
		Assert.assertNotNull("sqlSession can not be null, please check your configuration", sqlSession);
		return sqlSession.getMapper(daoClass);
	}
	@Override
	public int insert(E entity) {
		return getDao().insert(entity);
	}
	@Override
	public int update(E entity) {
		return getDao().update(entity);
	}
	@Override
	public int delete(K key) {
		return getDao().delete(key);
	}
	@Override
	public List<E> select() {
		return getDao().select();
	}

	@Override
	public E selectOneByKey(K key) {
		return getDao().selectOneByKey(key);
	}

	@Override
	public List<E> selectByCondition(E entity) {
		return getDao().selectByCondition(entity);
	}
	
}
