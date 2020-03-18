package com.wandun.modules.sys.dao;

import com.wandun.common.dao.CrudDao;
import com.wandun.common.persistence.annotation.MyBatisDao;
import com.wandun.modules.sys.entity.Role;
@MyBatisDao
public interface RoleDao extends CrudDao<Role, String> {
    Role getByName(Role role);

    Role getByEnname(Role role);

    /**
     * 维护角色与菜单权限关系
     *
     * @param role
     * @return
     */
    int deleteRoleMenu(Role role);

    int insertRoleMenu(Role role);

    /**
     * 维护角色与公司部门关系
     *
     * @param role
     * @return
     */
    int deleteRoleOffice(Role role);

    int insertRoleOffice(Role role);
}
