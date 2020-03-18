package com.wandun.modules.sys.web;


import com.google.common.collect.Lists;
import com.wandun.common.config.Global;
import com.wandun.common.utils.FileUtils;
import com.wandun.common.utils.StringUtils;
import com.wandun.common.utils.TwoDimensionCode;
import com.wandun.common.web.BaseController;
import com.wandun.common.web.Message;
import com.wandun.modules.sys.dao.UserDao;
import com.wandun.modules.sys.entity.Office;
import com.wandun.modules.sys.entity.Role;
import com.wandun.modules.sys.entity.SystemConfig;
import com.wandun.modules.sys.entity.User;
import com.wandun.modules.sys.service.OfficeService;
import com.wandun.modules.sys.service.SystemConfigService;
import com.wandun.modules.sys.service.SystemService;
import com.wandun.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 用户Controller
 *
 * @author 云南万盾科技有限公司
 * @version 2013-8-29
 */
@Slf4j
@Controller
@RequestMapping(value = "${adminPath}/sys/register")
public class RegisterController extends BaseController {


    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private OfficeService officeService;

    @Autowired
    private UserDao userDao;

    @ModelAttribute
    public User get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getUser(id);
        } else {
            return new User();
        }
    }


    @RequestMapping(value = {"index", ""})
    public String register(User user, Model model) {
        return "modules/sys/register";
    }


    @ResponseBody
    @RequestMapping(value = "registerUser")
    public Message registerUser(HttpServletRequest request, HttpServletResponse response, boolean mobileLogin, String randomCode, String roleName, User user, Model model, RedirectAttributes redirectAttributes) {


        //验证手机号是否已经注册

        if (userDao.findUniqueByProperty("mobile", user.getMobile()) != null) {
            // 如果是手机登录，则返回JSON字符串
            if (mobileLogin) {
                return Message.error("手机号已经被使用");
            } else {

                return Message.error("手机号已经被使用");
            }
        }

        //验证用户是否已经注册

        if (userDao.findUniqueByProperty("login_name", user.getLoginName()) != null) {
            // 如果是手机登录，则返回JSON字符串
            if (mobileLogin) {
                return Message.error("用户名已经被注册");
            } else {
                return Message.error("用户名已经被注册");
            }
        }

        //验证短信内容
        if (!randomCode.equals(request.getSession().getServletContext().getAttribute(user.getMobile()))) {
            // 如果是手机登录，则返回JSON字符串
            if (mobileLogin) {
                return Message.error("手机验证码不正确");
            } else {
                return Message.error("手机验证码不正确");
            }
        }


        // 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
        Role role = systemService.getRoleByEnname(roleName);
        String officeCode = "1000";
        if (roleName.equals("patient")) {
            officeCode = "1001";
        }
        Office office = officeService.getByCode(officeCode);
        // 密码MD5加密
        user.setPassword(systemService.entryptPassword(user.getPassword()));
        if (systemService.getUserByLoginName(user.getLoginName()) != null) {
            return Message.error("注册用户'" + user.getLoginName() + "'失败，用户名已存在");
        }
        // 角色数据有效性验证，过滤不在授权内的角色
        List<Role> roleList = Lists.newArrayList();
        roleList.add(role);
        user.setRoleList(roleList);
        //保存机构
        user.setCompany(office);
        user.setOffice(office);
        //生成用户二维码，使用登录名
        String realPath = Global.getUserfilesBaseDir() + Global.USERFILES
                + user.getId() + "/qrcode/";
        FileUtils.createDirectory(realPath);
        String name = user.getId() + ".png"; //encoderImgId此处二维码的图片名
        String filePath = realPath + name;  //存放路径
        TwoDimensionCode.encoderQRCode(user.getLoginName(), filePath, "png");//执行生成二维码
        user.setQrCode(request.getContextPath() + Global.USERFILES
                + user.getId() + "/qrcode/" + name);
        // 保存用户信息
        systemService.saveUser(user);
        // 清除当前用户缓存
        if (user.getLoginName().equals(UserUtils.getUser().getLoginName())) {
            UserUtils.clearCache();
            //UserUtils.getCacheMap().clear();
        }
        request.getSession().getServletContext().removeAttribute(user.getMobile());//清除验证码

        // 如果是手机登录，则返回JSON字符串
        if (mobileLogin) {

            return Message.error("注册用户'" + user.getLoginName() + "'成功");
        }
        return Message.error("注册用户'" + user.getLoginName() + "'成功");
    }


    /**
     * 获取验证码
     *
     * @param request
     * @param response
     * @param mobile
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "getRegisterCode")
    @ResponseBody
    public Message getRegisterCode(HttpServletRequest request, HttpServletResponse response, String mobile,
                                   Model model, RedirectAttributes redirectAttributes) {

        SystemConfig config = systemConfigService.get("1");

        //验证手机号是否已经注册
        if (userDao.findUniqueByProperty("mobile", mobile) != null) {

            return Message.error("手机号已经被使用");
        }

        return SUCCESS_MESSAGE;
    }


    /**
     * web端ajax验证手机验证码是否正确
     */
    @ResponseBody
    @RequestMapping(value = "validateMobileCode")
    public boolean validateMobileCode(HttpServletRequest request,
                                      String mobile, String randomCode) {
        if (randomCode.equals(request.getSession().getServletContext().getAttribute(mobile))) {
            return true;
        } else {
            return false;
        }
    }


}
