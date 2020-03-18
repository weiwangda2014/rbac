package com.wandun.modules.sys.service.impl;

import com.wandun.common.service.impl.CrudServiceImpl;
import com.wandun.modules.sys.dao.LogDao;
import com.wandun.modules.sys.entity.Log;
import com.wandun.modules.sys.entity.Office;
import com.wandun.modules.sys.service.LogService;
import com.wandun.modules.sys.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LogServiceImpl extends CrudServiceImpl<Log, String> implements LogService {
    @Autowired
    private LogDao logDao;
    @Override
    public void empty() {
        logDao.empty();
    }
}