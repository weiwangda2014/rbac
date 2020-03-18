package com.wandun.modules.monitor.service.impl;

import com.wandun.common.service.impl.CrudServiceImpl;
import com.wandun.modules.monitor.entity.Monitor;
import com.wandun.modules.monitor.service.MonitorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@Transactional(readOnly = true)
public class MonitorServiceImpl  extends CrudServiceImpl<Monitor, String> implements MonitorService {

}
