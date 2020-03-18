package com.wandun.modules.sys.dao;

import com.wandun.common.dao.CrudDao;
import com.wandun.common.persistence.annotation.MyBatisDao;
import com.wandun.modules.sys.entity.Dict;

import java.util.List;

@MyBatisDao
public interface DictDao extends CrudDao<Dict, String> {
    List<String> findTypeList(Dict dict);
}
