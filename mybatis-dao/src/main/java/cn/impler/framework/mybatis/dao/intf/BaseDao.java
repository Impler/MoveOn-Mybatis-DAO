package cn.impler.framework.mybatis.dao.intf;

import java.util.List;

/**
 * the base interface of the dao level, all specific business dao should extend
 * from it
 * @author impler
 * @date 2016-11-29
 * @param <E> entity class
 * @param <K> ID class
 */
public interface BaseDao<E, K> {

	/**
	 * insert an entity
	 * @param entity
	 * @return influenced rows
	 */
	public int insert(E entity);

	/**
	 * update an entity
	 * @param entity
	 * @return influenced rows
	 */
	public int update(E entity);

	/**
	 * delete a record by id
	 * @param key
	 * @return influenced rows
	 */
	public int delete(K key);

	/**
	 * query all records
	 * @return entity list
	 */
	public List<E> select();

	/**
	 * query record by id
	 * @param key
	 * @return an entity
	 */
	public E selectOneByKey(K key);

	/**
	 * query records by some conditions
	 * @param entity
	 * @return entity list
	 */
	public List<E> selectByCondition(E entity);
}
