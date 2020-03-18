package com.wandun.modules.sys.service.impl;

import com.wandun.common.service.impl.TreeServiceImpl;
import com.wandun.modules.sys.entity.Menu;
import com.wandun.modules.sys.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MenuServiceImpl extends TreeServiceImpl<Menu,String> implements MenuService {

}