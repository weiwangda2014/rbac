package com.wandun.modules.sys.service;

import com.wandun.common.service.CrudService;
import com.wandun.modules.sys.entity.Log;
import com.wandun.modules.sys.entity.Office;

public interface LogService extends CrudService<Log, String> {

    void empty();
}