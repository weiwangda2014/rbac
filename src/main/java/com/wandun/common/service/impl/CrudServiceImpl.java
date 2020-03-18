package com.wandun.common.service.impl;

import com.wandun.common.dao.CrudDao;
import com.wandun.common.persistence.DataEntity;
import com.wandun.common.persistence.Page;
import com.wandun.common.service.CrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public abstract class CrudServiceImpl<T extends DataEntity<T, ID>, ID extends Serializable> implements CrudService<T, ID> {

    /**
     * BaseDao
     */
    @Autowired
    private CrudDao<T, ID> crudDao;

    @Transactional(readOnly = true)
    public T get(ID id) {
        return crudDao.get(id);
    }

    /**
     * 获取单条数据
     *
     * @param entity
     * @return
     */
    @Transactional(readOnly = true)
    public T get(T entity) {
        return crudDao.get(entity);
    }

    /**
     * 查询列表数据
     *
     * @param entity
     * @return
     */
    @Transactional(readOnly = true)
    public List<T> findList(T entity) {
        return crudDao.findList(entity);
    }

    /**
     * 查询分页数据
     *
     * @param page   分页对象
     * @param entity
     * @return
     */
    @Transactional(readOnly = true)
    public Page<T> findPage(Page<T> page, T entity) {
        entity.setPage(page);
        page.setList(crudDao.findList(entity));
        return page;
    }

    /**
     * 保存数据（插入或更新）
     *
     * @param entity
     */
    @Transactional
    public void save(T entity) {
        if (entity.getIsNewRecord()) {
            entity.preInsert();
            crudDao.insert(entity);
        } else {
            entity.preUpdate();
            crudDao.update(entity);
        }
    }

    @Transactional
    public void delete(ID entity) {
        crudDao.delete(entity);
    }
    /**
     * 删除数据
     *
     * @param entity
     */
    @Transactional
    public void delete(T entity) {
        crudDao.delete(entity);
    }

    @Transactional
    public void deleteAll(List<ID> list) {
        crudDao.deleteAll(list);
    }

    /**
     * 获取单条数据
     *
     * @param propertyName
     * @param value
     * @return
     */
    @Transactional(readOnly = true)
    public T findUniqueByProperty(String propertyName, Object value) {
        return crudDao.findUniqueByProperty(propertyName, value);
    }

}
