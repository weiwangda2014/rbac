package com.wandun.modules.sys.service;

import com.wandun.common.service.CrudService;
import com.wandun.modules.sys.entity.Dict;
import com.wandun.modules.sys.entity.Office;

import java.util.List;

public interface DictService extends CrudService<Dict, String> {

    List<String> findTypeList();
}