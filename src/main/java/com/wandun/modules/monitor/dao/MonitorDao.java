/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.modules.monitor.dao;

import com.wandun.common.dao.CrudDao;
import com.wandun.common.persistence.annotation.MyBatisDao;
import com.wandun.modules.monitor.entity.Monitor;

/**
 * 系统监控DAO接口
 *
 * @author liugf
 * @version 2016-02-07
 */
@MyBatisDao
public interface MonitorDao extends CrudDao<Monitor, String> {

}