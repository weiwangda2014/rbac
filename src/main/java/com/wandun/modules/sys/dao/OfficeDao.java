package com.wandun.modules.sys.dao;

import com.wandun.common.dao.TreeDao;
import com.wandun.common.persistence.annotation.MyBatisDao;
import com.wandun.modules.sys.entity.Office;
@MyBatisDao
public interface OfficeDao extends TreeDao<Office, String> {
    Office getByCode(String code);
}
