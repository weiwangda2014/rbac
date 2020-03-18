package com.wandun.modules.worker.service.impl;

import com.baidu.fsg.uid.UidGenerator;
import com.wandun.modules.worker.service.UidGenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
@Service
@Transactional(readOnly = true)
public class UidGenServiceImpl implements UidGenService {

    @Resource(name = "defaultUidGenerator")
    private UidGenerator uidGenerator;
    public Long getUid() {
        return uidGenerator.getUID();
    }
}
