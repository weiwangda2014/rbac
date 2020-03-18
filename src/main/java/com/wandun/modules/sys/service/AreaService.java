package com.wandun.modules.sys.service;

import com.wandun.common.service.TreeService;
import com.wandun.modules.sys.entity.Area;

import java.util.List;

public interface AreaService extends TreeService<Area, String> {


    List<Area> findRoot();

    List<Area> findChildsByParentId(String parentid);
}