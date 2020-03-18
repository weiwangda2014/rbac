package com.wandun.common.service;

import com.wandun.common.persistence.DataEntity;
import com.wandun.common.persistence.Page;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface CrudService<T extends DataEntity<T, ID>, ID extends Serializable> extends BaseService {

    T get(ID id);

    T get(T entity);

    List<T> findList(T entity);

    Page<T> findPage(Page<T> page, T entity);

    void save(T entity);

    void delete(T entity);

    void delete(ID id);

    T findUniqueByProperty(String propertyName, Object value);

    void deleteAll(List<ID> list);
}
