package com.wandun.common.service.impl;

import com.wandun.common.dao.TreeDao;
import com.wandun.common.persistence.TreeEntity;
import com.wandun.common.service.ServiceException;
import com.wandun.common.service.TreeService;
import com.wandun.common.utils.Reflections;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

public abstract class TreeServiceImpl<T extends TreeEntity<T, ID>, ID extends Serializable> extends CrudServiceImpl<T, ID> implements TreeService<T, ID> {

    /**
     * BaseDao
     */

    private TreeDao<T, ID> treeDao;
    @Autowired
    public void setTreeDao(TreeDao<T, ID> treeDao) {
        this.treeDao = treeDao;
    }

    @Transactional
    public void save(T entity) {

        Class<T> entityClass = Reflections.getClassGenericType(getClass(), 1);

        // 如果没有设置父节点，则代表为跟节点，有则获取父节点实体
        if (entity.getParent() == null || entity.getParentId() == null
                || "0".equals(entity.getParentId())) {
            entity.setParent(null);
        } else {
            entity.setParent(super.get((ID) entity.getParentId()));
        }
        if (entity.getParent() == null) {
            T parentEntity;
            try {
                parentEntity = entityClass.getConstructor(String.class).newInstance("0");
            } catch (Exception e) {
                throw new ServiceException(e);
            }
            entity.setParent(parentEntity);
            entity.getParent().setPath(StringUtils.EMPTY);
        }

        // 获取修改前的parentIds，用于更新子节点的parentIds
        String oldParentIds = entity.getPath();

        // 设置新的父节点串
        entity.setPath(entity.getParent().getPath() + entity.getParent().getId() + ",");

        // 保存或更新实体
        super.save(entity);

        // 更新子节点 parentIds
        T o = null;
        try {
            o = entityClass.newInstance();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
        o.setPath("%," + entity.getId() + ",%");
        List<T> list = treeDao.findByPathLike(o);
        for (T e : list) {
            if (e.getPath() != null && oldParentIds != null) {
                e.setPath(e.getPath().replace(oldParentIds, entity.getPath()));
                preUpdateChild(entity, e);
                treeDao.updatePath(e);
            }
        }

    }

    /**
     * 预留接口，用户更新子节前调用
     *
     * @param childEntity
     */
    protected void preUpdateChild(T entity, T childEntity) {

    }
}
