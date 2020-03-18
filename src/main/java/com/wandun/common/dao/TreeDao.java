/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.wandun.common.dao;

import com.wandun.common.dao.CrudDao;
import com.wandun.common.persistence.TreeEntity;

import java.io.Serializable;
import java.util.List;

/**
 * DAO支持类实现
 *
 * @param <T>
 * @author jeeplus
 * @version 2014-05-16
 */
public interface TreeDao<T extends TreeEntity<T, ID>, ID extends Serializable> extends CrudDao<T, ID> {

    /**
     * 找到所有子节点
     *
     * @param entity
     * @return
     */
    List<T> findByPathLike(T entity);

    /**
     * 更新所有父节点字段
     *
     * @param entity
     * @return
     */
    int updatePath(T entity);

}