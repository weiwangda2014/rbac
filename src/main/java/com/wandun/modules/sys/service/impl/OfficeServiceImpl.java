package com.wandun.modules.sys.service.impl;

import com.wandun.common.service.impl.TreeServiceImpl;
import com.wandun.modules.sys.dao.OfficeDao;
import com.wandun.modules.sys.entity.Office;
import com.wandun.modules.sys.service.OfficeService;
import com.wandun.modules.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OfficeServiceImpl extends TreeServiceImpl<Office, String> implements OfficeService {
    @Autowired
    private OfficeDao officeDao;

    public List<Office> findList(Boolean isAll) {
        if (isAll != null && isAll) {
            return UserUtils.getOfficeAllList();
        } else {
            return UserUtils.getOfficeList();
        }
    }

    @Override
    public List<Office> findAll() {
        return UserUtils.getOfficeList();
    }

    @Transactional(readOnly = true)
    public Office getByCode(String code) {
        return officeDao.getByCode(code);
    }
}