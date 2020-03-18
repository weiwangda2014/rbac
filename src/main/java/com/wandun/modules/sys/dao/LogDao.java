package com.wandun.modules.sys.dao;

import com.wandun.common.dao.CrudDao;
import com.wandun.common.persistence.annotation.MyBatisDao;
import com.wandun.modules.sys.entity.Log;
@MyBatisDao
public interface LogDao extends CrudDao<Log, String> {
    void empty();
}
