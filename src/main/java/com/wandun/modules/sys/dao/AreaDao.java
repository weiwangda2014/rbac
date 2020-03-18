package com.wandun.modules.sys.dao;

import com.wandun.common.dao.TreeDao;
import com.wandun.common.persistence.annotation.MyBatisDao;
import com.wandun.modules.sys.entity.Area;

import java.util.List;

@MyBatisDao
public interface AreaDao extends TreeDao<Area, String> {
    List<Area> findRoot(Area area);

    List<Area> findChildsByParentId(Area area);
}
