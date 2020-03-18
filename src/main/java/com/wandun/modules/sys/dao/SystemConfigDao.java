package com.wandun.modules.sys.dao;

import com.wandun.common.dao.CrudDao;
import com.wandun.common.persistence.annotation.MyBatisDao;
import com.wandun.modules.sys.entity.SystemConfig;
@MyBatisDao
public interface SystemConfigDao  extends CrudDao<SystemConfig, String> {
}
