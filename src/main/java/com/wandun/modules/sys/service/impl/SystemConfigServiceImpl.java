package com.wandun.modules.sys.service.impl;

import com.wandun.common.service.impl.CrudServiceImpl;
import com.wandun.modules.sys.entity.Office;
import com.wandun.modules.sys.entity.SystemConfig;
import com.wandun.modules.sys.service.OfficeService;
import com.wandun.modules.sys.service.SystemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SystemConfigServiceImpl extends CrudServiceImpl<SystemConfig, String> implements SystemConfigService {

}