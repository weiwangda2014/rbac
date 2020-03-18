package com.wandun.modules.sys.service.impl;

import com.wandun.common.service.impl.CrudServiceImpl;
import com.wandun.modules.sys.dao.DictDao;
import com.wandun.modules.sys.entity.Dict;
import com.wandun.modules.sys.entity.Office;
import com.wandun.modules.sys.service.DictService;
import com.wandun.modules.sys.service.OfficeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DictServiceImpl extends CrudServiceImpl<Dict, String> implements DictService {
    @Autowired
    private DictDao dictDao;
    @Override
    public List<String> findTypeList() {
        return dictDao.findTypeList(new Dict());
    }
}