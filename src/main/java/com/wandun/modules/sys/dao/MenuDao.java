package com.wandun.modules.sys.dao;

import com.wandun.common.dao.TreeDao;
import com.wandun.common.persistence.annotation.MyBatisDao;
import com.wandun.modules.sys.entity.Menu;

import java.util.List;
@MyBatisDao
public interface MenuDao extends TreeDao<Menu, String> {
    List<Menu> findByParentIdsLike(Menu menu);

    List<Menu> findByUserId(Menu menu);

    int updateParentIds(Menu menu);

    int updateSort(Menu menu);
}
