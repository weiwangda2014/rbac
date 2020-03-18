/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.modules.sys.entity;

import com.google.common.collect.Lists;
import com.wandun.common.config.Global;
import com.wandun.common.persistence.DataEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import java.util.List;

/**
 * 角色Entity
 *
 * @author WanDun
 * @version 2013-12-05
 */
public class Role extends DataEntity<Role, String> {

    private static final long serialVersionUID = 1L;
    @Getter
    @Setter
    private Office office;    // 归属机构
    @Getter
    @Setter
    @Length(min = 1, max = 100)
    private String name;    // 角色名称
    @Getter
    @Setter
    @Length(min = 1, max = 100)
    private String enname;    // 英文名称
    @Getter
    @Setter
    @Length(min = 1, max = 100)
    private String roleType;// 权限类型
    @Getter
    @Setter
    private String dataScope;// 数据范围
    @Getter
    @Setter
    private String oldName;    // 原角色名称
    @Getter
    @Setter
    private String oldEnname;    // 原英文名称
    @Getter
    @Setter
    private String sysData;        //是否是系统数据
    @Getter
    @Setter
    private String useable;        //是否是可用
    @Getter
    @Setter
    private User user;        // 根据用户ID查询角色列表

    //	private List<User> userList = Lists.newArrayList(); // 拥有用户列表
    private List<Menu> menuList = Lists.newArrayList(); // 拥有菜单列表
    @Getter
    @Setter
    private List<Office> officeList = Lists.newArrayList(); // 按明细设置数据范围

    // 数据范围（1：所有数据；2：所在公司及以下数据；3：所在公司数据；4：所在部门及以下数据；5：所在部门数据；8：仅本人数据；9：按明细设置）
    public static final String DATA_SCOPE_ALL = "1";
    public static final String DATA_SCOPE_COMPANY_AND_CHILD = "2";
    public static final String DATA_SCOPE_COMPANY = "3";
    public static final String DATA_SCOPE_OFFICE_AND_CHILD = "4";
    public static final String DATA_SCOPE_OFFICE = "5";
    public static final String DATA_SCOPE_SELF = "8";
    public static final String DATA_SCOPE_CUSTOM = "9";

    public Role() {
        super();
        this.dataScope = DATA_SCOPE_SELF;
        this.useable = Global.YES;
    }

    public Role(String id) {
        super(id);
    }

    public Role(User user) {
        this();
        this.user = user;
    }


    public List<Menu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }

    public List<String> getMenuIdList() {
        List<String> menuIdList = Lists.newArrayList();
        for (Menu menu : menuList) {
            menuIdList.add(menu.getId());
        }
        return menuIdList;
    }

    public void setMenuIdList(List<String> menuIdList) {
        menuList = Lists.newArrayList();
        for (String menuId : menuIdList) {
            Menu menu = new Menu();
            menu.setId(menuId);
            menuList.add(menu);
        }
    }

    public String getMenuIds() {
        return StringUtils.join(getMenuIdList(), ",");
    }

    public void setMenuIds(String menuIds) {
        menuList = Lists.newArrayList();
        if (menuIds != null) {
            String[] ids = StringUtils.split(menuIds, ",");
            setMenuIdList(Lists.newArrayList(ids));
        }
    }


    public List<String> getOfficeIdList() {
        List<String> officeIdList = Lists.newArrayList();
        for (Office office : officeList) {
            officeIdList.add(office.getId());
        }
        return officeIdList;
    }

    public void setOfficeIdList(List<String> officeIdList) {
        officeList = Lists.newArrayList();
        for (String officeId : officeIdList) {
            Office office = new Office();
            office.setId(officeId);
            officeList.add(office);
        }
    }

    public String getOfficeIds() {
        return StringUtils.join(getOfficeIdList(), ",");
    }

    public void setOfficeIds(String officeIds) {
        officeList = Lists.newArrayList();
        if (officeIds != null) {
            String[] ids = StringUtils.split(officeIds, ",");
            setOfficeIdList(Lists.newArrayList(ids));
        }
    }

    /**
     * 获取权限字符串列表
     */
    public List<String> getPermissions() {
        List<String> permissions = Lists.newArrayList();
        for (Menu menu : menuList) {
            if (menu.getPermission() != null && !"".equals(menu.getPermission())) {
                permissions.add(menu.getPermission());
            }
        }
        return permissions;
    }

}
