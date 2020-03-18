/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.modules.sys.entity;

import com.wandun.common.persistence.DataEntity;
import com.wandun.common.utils.excel.annotation.ExcelField;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 系统配置Entity
 *
 * @author liugf
 * @version 2016-02-07
 */
@Data
public class SystemConfig extends DataEntity<SystemConfig,String> {

    private static final long serialVersionUID = 1L;
    @Length(max = 64, message = "邮箱服务器地址长度必须介于 0 和 64 之间")
    @ExcelField(title = "邮箱服务器地址", align = 2, sort = 1)
    private String smtp;        // 邮箱服务器地址
    @Length(max = 64, message = "邮箱服务器端口长度必须介于 0 和 64 之间")
    @ExcelField(title = "邮箱服务器端口", align = 2, sort = 2)
    private String port;        // 邮箱服务器端口
    @Length(max = 64, message = "系统邮箱地址长度必须介于 0 和 64 之间")
    @ExcelField(title = "系统邮箱地址", align = 2, sort = 3)
    private String mailName;        // 系统邮箱地址
    @Length(max = 64, message = "系统邮箱密码长度必须介于 0 和 64 之间")
    @ExcelField(title = "系统邮箱密码", align = 2, sort = 4)
    private String mailPassword;        // 系统邮箱密码
    @Length(max = 64, message = "短信用户名长度必须介于 0 和 64 之间")
    @ExcelField(title = "短信用户名", align = 2, sort = 5)
    private String smsName;        // 短信用户名
    @Length(max = 64, message = "短信密码长度必须介于 0 和 64 之间")
    @ExcelField(title = "短信密码", align = 2, sort = 6)
    private String smsPassword;        // 短信密码
    private boolean test = false;

    public SystemConfig() {
        super();
    }

    public SystemConfig(String id) {
        super(id);
    }

}