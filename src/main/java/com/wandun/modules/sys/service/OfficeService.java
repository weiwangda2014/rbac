package com.wandun.modules.sys.service;

import com.wandun.common.service.TreeService;
import com.wandun.modules.sys.entity.Office;

import java.util.List;

public interface OfficeService extends TreeService<Office, String> {

    List<Office> findList(Boolean isAll);

    List<Office> findAll();

    Office getByCode(String officeCode);
}