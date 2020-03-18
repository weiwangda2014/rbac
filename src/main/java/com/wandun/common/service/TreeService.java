
package com.wandun.common.service;

import com.wandun.common.dao.TreeDao;
import com.wandun.common.persistence.TreeEntity;

import java.io.Serializable;

/**
 * Service基类
 *
 * @author 云南万盾科技有限公司
 * @version 2014-05-16
 */

public interface TreeService<T extends TreeEntity<T, ID>, ID extends Serializable> extends CrudService<T, ID> {

}
