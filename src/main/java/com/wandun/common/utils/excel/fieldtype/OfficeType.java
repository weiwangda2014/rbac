/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.common.utils.excel.fieldtype;

import com.wandun.common.utils.StringUtils;
import com.wandun.modules.sys.entity.Office;
import com.wandun.modules.sys.utils.UserUtils;

/**
 * 字段类型转换
 *
 * @author 云南万盾科技有限公司
 * @version 2013-03-10
 */
public class OfficeType {

    /**
     * 获取对象值（导入）
     */
    public static Object getValue(String val) {
        for (Office e : UserUtils.getOfficeList()) {
            if (StringUtils.trimToEmpty(val).equals(e.getName())) {
                return e;
            }
        }
        return null;
    }

    /**
     * 设置对象值（导出）
     */
    public static String setValue(Object val) {
        if (val != null && ((Office) val).getName() != null) {
            return ((Office) val).getName();
        }
        return "";
    }
}
