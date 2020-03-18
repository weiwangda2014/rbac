/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.common.utils.excel.fieldtype;

import com.google.common.collect.Lists;
import com.wandun.common.utils.Collections3;
import com.wandun.common.utils.SpringContextHolder;
import com.wandun.common.utils.StringUtils;
import com.wandun.modules.sys.entity.Role;
import com.wandun.modules.sys.service.SystemService;

import java.util.List;

/**
 * 字段类型转换
 *
 * @author 云南万盾科技有限公司
 * @version 2013-5-29
 */
public class RoleListType {

    private static SystemService systemService = SpringContextHolder.getBean(SystemService.class);

    /**
     * 获取对象值（导入）
     */
    public static Object getValue(String val) {
        List<Role> roleList = Lists.newArrayList();
        List<Role> allRoleList = systemService.findAllRole();
        for (String s : StringUtils.split(val, ",")) {
            for (Role e : allRoleList) {
                if (StringUtils.trimToEmpty(s).equals(e.getName())) {
                    roleList.add(e);
                }
            }
        }
        return roleList.size() > 0 ? roleList : null;
    }

    /**
     * 设置对象值（导出）
     */
    public static String setValue(Object val) {
        if (val != null) {
            @SuppressWarnings("unchecked")
            List<Role> roleList = (List<Role>) val;
            return Collections3.extractToString(roleList, "name", ", ");
        }
        return "";
    }

}
