/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.modules.sys.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.wandun.common.config.Global;
import com.wandun.common.persistence.DataEntity;
import com.wandun.common.utils.Collections3;
import com.wandun.common.utils.excel.annotation.ExcelField;
import com.wandun.common.utils.excel.fieldtype.RoleListType;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * 用户Entity
 *
 * @author WanDun
 * @version 2013-12-05
 */
@Data
public class User extends DataEntity<User, String> {

    private static final long serialVersionUID = 1L;
    @JsonIgnore
    @NotNull(message = "归属公司不能为空")
    @ExcelField(title = "归属公司", align = 2, sort = 20)
    private Office company;    // 归属公司
    @JsonIgnore
    @NotNull(message = "归属部门不能为空")
    @ExcelField(title = "归属部门", align = 2, sort = 25)
    private Office office;    // 归属部门
    @Length(min = 1, max = 100, message = "登录名长度必须介于 1 和 100 之间")
    @ExcelField(title = "登录名", align = 2, sort = 30)
    private String loginName;// 登录名
    @JsonIgnore
    @Length(min = 1, max = 100, message = "密码长度必须介于 1 和 100 之间")
    private String password;// 密码
    @Length(min = 1, max = 100, message = "工号长度必须介于 1 和 100 之间")
    @ExcelField(title = "工号", align = 2, sort = 45)
    private String no;        // 工号
    @Length(min = 1, max = 100, message = "姓名长度必须介于 1 和 100 之间")
    @ExcelField(title = "姓名", align = 2, sort = 40)
    private String name;    // 姓名
    @Email(message = "邮箱格式不正确")
    @Length(min = 0, max = 200, message = "邮箱长度必须介于 1 和 200 之间")
    @ExcelField(title = "邮箱", align = 2, sort = 50)
    private String email;    // 邮箱
    @Length(min = 0, max = 200, message = "电话长度必须介于 1 和 200 之间")
    @ExcelField(title = "电话", align = 2, sort = 60)
    private String phone;    // 电话
    @Length(min = 0, max = 200, message = "手机长度必须介于 1 和 200 之间")
    @ExcelField(title = "手机", align = 2, sort = 70)
    private String mobile;    // 手机
    @Length(min = 0, max = 100, message = "用户类型长度必须介于 1 和 100 之间")
    @ExcelField(title = "用户类型", align = 2, sort = 80, dictType = "sys_user_type")
    private String userType;// 用户类型
    @ExcelField(title = "最后登录IP", type = 2, align = 1, sort = 100)
    private String loginIp;    // 最后登陆IP

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelField(title = "最后登录日期", type = 2, align = 1, sort = 110)
    private Date loginDate;    // 最后登陆日期
    private String loginFlag;    // 是否允许登陆
    private String photo;    // 头像
    private String qrCode;    //二维码
    private String oldLoginName;// 原登录名
    private String newPassword;    // 新密码
    private String sign;//签名
    private String nodeId;
    @ExcelField(title = "身份证号", align = 2, sort = 130)
    private String carded;//身份证号
    @ExcelField(title = "地址", align = 2, sort = 140)
    private String address;//地址
    @ExcelField(title = "学历", align = 2, sort = 160, dictType = "education_type")
    private String education;//学历
    @ExcelField(title = "入职时间", align = 2, sort = 180)
    private Date thetime;//入职时间

    private String oldLoginIp;    // 上次登陆IP
    private Date oldLoginDate;    // 上次登陆日期

    private Role role;    // 根据角色查询用户条件

    @JsonIgnore
    @ExcelField(title = "拥有角色", align = 1, sort = 800, fieldType = RoleListType.class)
    private List<Role> roleList = Lists.newArrayList(); // 拥有角色列表

    public User() {
        super();
        this.loginFlag = Global.YES;
    }

    public User(String id) {
        super(id);
    }

    public User(String id, String loginName) {
        super(id);
        this.loginName = loginName;
    }

    public User(Role role) {
        super();
        this.role = role;
    }


    @ExcelField(title = "备注", align = 1, sort = 900)
    public String getRemarks() {
        return super.getRemarks();
    }


    @ExcelField(title = "创建时间", type = 0, align = 1, sort = 90)
    public Date getCreateDate() {
        return super.getCreateDate();
    }


    public String getOldLoginIp() {
        if (oldLoginIp == null) {
            return loginIp;
        }
        return oldLoginIp;
    }

    public void setOldLoginIp(String oldLoginIp) {
        this.oldLoginIp = oldLoginIp;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date getOldLoginDate() {
        if (oldLoginDate == null) {
            return loginDate;
        }
        return oldLoginDate;
    }


    @JsonIgnore
    public List<String> getRoleIdList() {
        List<String> roleIdList = Lists.newArrayList();
        for (Role role : roleList) {
            roleIdList.add(role.getId());
        }
        return roleIdList;
    }

    public void setRoleIdList(List<String> roleIdList) {
        roleList = Lists.newArrayList();
        for (String roleId : roleIdList) {
            Role role = new Role();
            role.setId(roleId);
            roleList.add(role);
        }
    }

    /**
     * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
     */
    public String getRoleNames() {
        return Collections3.extractToString(roleList, "name", ",");
    }

    public boolean isAdmin() {
        return isAdmin(this.getId());
    }

    public static boolean isAdmin(String id) {
        return id != null && "1".equals(id);
    }


}