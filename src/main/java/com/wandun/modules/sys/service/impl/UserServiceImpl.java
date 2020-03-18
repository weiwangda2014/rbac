package com.wandun.modules.sys.service.impl;

import com.wandun.common.service.impl.CrudServiceImpl;
import com.wandun.modules.sys.entity.User;
import com.wandun.modules.sys.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl extends CrudServiceImpl<User, String> implements UserService {

}