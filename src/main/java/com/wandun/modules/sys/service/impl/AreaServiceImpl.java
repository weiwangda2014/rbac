package com.wandun.modules.sys.service.impl;

import com.wandun.common.service.impl.TreeServiceImpl;
import com.wandun.modules.sys.dao.AreaDao;
import com.wandun.modules.sys.entity.Area;
import com.wandun.modules.sys.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AreaServiceImpl extends TreeServiceImpl<Area, String> implements AreaService {

    @Autowired
    private AreaDao dao;

    @Transactional(readOnly = true)
    public List<Area> findRoot() {
        Area area = new Area();
        return dao.findRoot(area);
    }

    @Transactional(readOnly = true)
    public List<Area> findChildsByParentId(String parentid) {
        Area area = new Area();
        area.setId(parentid);
        area.setParent(area);
        return dao.findChildsByParentId(area);
    }
}