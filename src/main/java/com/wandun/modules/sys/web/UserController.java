/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.modules.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wandun.common.beanvalidator.BeanValidators;
import com.wandun.common.config.Global;
import com.wandun.common.persistence.Page;
import com.wandun.common.utils.DateUtils;
import com.wandun.common.utils.FileUtils;
import com.wandun.common.utils.StringUtils;
import com.wandun.common.utils.TwoDimensionCode;
import com.wandun.common.utils.excel.ExportExcel;
import com.wandun.common.utils.excel.ImportExcel;
import com.wandun.common.web.BaseController;
import com.wandun.common.web.Message;
import com.wandun.modules.sys.dao.UserDao;
import com.wandun.modules.sys.entity.Office;
import com.wandun.modules.sys.entity.Role;
import com.wandun.modules.sys.entity.User;
import com.wandun.modules.sys.service.SystemConfigService;
import com.wandun.modules.sys.service.SystemService;
import com.wandun.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户Controller
 *
 * @author WanDun
 * @version 2013-8-29
 */
@Slf4j
@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SystemService systemService;
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

    @RequiresPermissions("sys:user:index")
    @RequestMapping(value = {"index"})
    public String index() {
        return "modules/sys/userIndex";
    }

    @RequiresPermissions("sys:user:index")
    @RequestMapping(value = {"list", ""})
    public String list(User user, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<User> page = systemService.findUser(new Page<>(request, response), user);
        model.addAttribute("page", page);
        return "modules/sys/userList";
    }


    @RequiresPermissions(value = {"sys:user:view", "sys:user:add", "sys:user:edit"}, logical = Logical.OR)
    @RequestMapping(value = "form")
    public String form(User user, Model model) {
        if (user.getCompany() == null || user.getCompany().getId() == null) {
            user.setCompany(UserUtils.getUser().getCompany());
        }
        if (user.getOffice() == null || user.getOffice().getId() == null) {
            user.setOffice(UserUtils.getUser().getOffice());
        }
        model.addAttribute("user", user);
        model.addAttribute("allRoles", systemService.findAllRole());
        return "modules/sys/userForm";
    }

    @ResponseBody
    @RequiresPermissions(value = {"sys:user:add", "sys:user:edit"}, logical = Logical.OR)
    @RequestMapping(value = "save")
    public Message save(User user, HttpServletRequest request) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        // 修正引用赋值问题，不知道为何，Company和Office引用的一个实例地址，修改了一个，另外一个跟着修改。
        user.setCompany(new Office(request.getParameter("company.id")));
        user.setOffice(new Office(request.getParameter("office.id")));
        // 如果新密码为空，则不更换密码
        if (StringUtils.isNotBlank(user.getNewPassword())) {
            user.setPassword(systemService.entryptPassword(user.getNewPassword()));
        }

        if (!"true".equals(checkLoginName(user.getOldLoginName(), user.getLoginName()))) {
            return Message.error("保存用户'" + user.getLoginName() + "'失败，登录名已存在");
        }
        // 角色数据有效性验证，过滤不在授权内的角色
        List<Role> roleList = Lists.newArrayList();
        List<String> roleIdList = user.getRoleIdList();
        for (Role r : systemService.findAllRole()) {
            if (roleIdList.contains(r.getId())) {
                roleList.add(r);
            }
        }
        user.setRoleList(roleList);
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
        }
        return SUCCESS_MESSAGE;
    }

    @ResponseBody
    @RequiresPermissions("sys:user:del")
    @RequestMapping(value = "delete")
    public Message delete(User user) {
        StringBuffer msg = new StringBuffer();
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        } else if (UserUtils.getUser().getId().equals(user.getId())) {
            msg.append("删除用户失败, 不允许删除当前用户");
        } else if (User.isAdmin(user.getId())) {
            msg.append("删除用户失败, 不允许删除超级管理员用户");
        } else {
            systemService.deleteUser(user);
            msg.append("删除用户成功");
        }
        return Message.success(msg.toString());
    }

    /**
     * 批量删除用户
     *
     * @return
     */
    @ResponseBody
    @RequiresPermissions("sys:user:del")
    @RequestMapping(value = "deleteAll", method = RequestMethod.POST)
    public Message deleteAll(String ids) {
        String idArray[] = ids.split(",");
        StringBuffer msg = new StringBuffer();
        for (String id : idArray) {
            User user = systemService.getUser(id);
            if (Global.isDemoMode()) {
                return Message.error("演示模式，不允许操作！");
            } else if (UserUtils.getUser().getId().equals(user.getId())) {
                msg.append("删除用户失败, 不允许删除当前用户");
            } else if (User.isAdmin(user.getId())) {
                msg.append("删除用户失败, 不允许删除超级管理员用户");
            } else {
                systemService.deleteUser(user);
                msg.append("删除用户成功");
            }
        }
        return Message.success(msg.toString());
    }

    /**
     * 导出用户数据
     *
     * @param user
     * @param request
     * @param response
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("sys:user:export")
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public String exportFile(User user, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "用户数据" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            Page<User> page = systemService.findUser(new Page<User>(request, response, -1), user);
            new ExportExcel("用户数据", User.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出用户失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 导入用户数据
     *
     * @param file
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("sys:user:import")
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/user/list?repage";
        }
        try {
            int successNum = 0;
            int failureNum = 0;
            StringBuilder failureMsg = new StringBuilder();
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<User> list = ei.getDataList(User.class);
            for (User user : list) {
                try {
                    if ("true".equals(checkLoginName("", user.getLoginName()))) {
                        user.setPassword(systemService.entryptPassword("123456"));
                        BeanValidators.validateWithException(validator, user);
                        systemService.saveUser(user);
                        successNum++;
                    } else {
                        failureMsg.append("<br/>登录名 " + user.getLoginName() + " 已存在; ");
                        failureNum++;
                    }
                } catch (ConstraintViolationException ex) {
                    failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：");
                    List<String> messageList = BeanValidators.extractPropertyAndMessageAsList(ex, ": ");
                    for (String message : messageList) {
                        failureMsg.append(message + "; ");
                        failureNum++;
                    }
                } catch (Exception ex) {
                    failureMsg.append("<br/>登录名 " + user.getLoginName() + " 导入失败：" + ex.getMessage());
                }
            }
            if (failureNum > 0) {
                failureMsg.insert(0, "，失败 " + failureNum + " 条用户，导入信息如下：");
            }
            addMessage(redirectAttributes, "已成功导入 " + successNum + " 条用户" + failureMsg);
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入用户失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 下载导入用户数据模板
     *
     * @param response
     * @param redirectAttributes
     * @return
     */
    @RequiresPermissions("sys:user:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "用户数据导入模板.xlsx";
            List<User> list = Lists.newArrayList();
            list.add(UserUtils.getUser());
            new ExportExcel("用户数据", User.class, 2).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + adminPath + "/sys/user/list?repage";
    }

    /**
     * 验证登录名是否有效
     *
     * @param oldLoginName
     * @param loginName
     * @return
     */
    @ResponseBody
    @RequiresPermissions(value = {"sys:user:add", "sys:user:edit"}, logical = Logical.OR)
    @RequestMapping(value = "checkLoginName")
    public String checkLoginName(String oldLoginName, String loginName) {
        if (loginName != null && loginName.equals(oldLoginName)) {
            return "true";
        } else if (loginName != null && systemService.getUserByLoginName(loginName) == null) {
            return "true";
        }
        return "false";
    }

    /**
     * 用户信息显示
     *
     * @param model
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "info")
    public String info(Model model) {
        User currentUser = UserUtils.getUser();
        model.addAttribute("user", currentUser);
        model.addAttribute("Global", Global.getInstance());
        return "modules/sys/userInfo";
    }

    /**
     * 用户信息显示编辑保存
     *
     * @param user
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "infoEdit")
    public Message infoEdit(User user, boolean __ajax) {
        User currentUser = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getName())) {
            if (Global.isDemoMode()) {
                Message.warn("演示模式，不允许操作！");
            }
            if (user.getName() != null)
                currentUser.setName(user.getName());
            if (user.getEmail() != null)
                currentUser.setEmail(user.getEmail());
            if (user.getPhone() != null)
                currentUser.setPhone(user.getPhone());
            if (user.getMobile() != null)
                currentUser.setMobile(user.getMobile());
            if (user.getRemarks() != null)
                currentUser.setRemarks(user.getRemarks());
            systemService.updateUserInfo(currentUser);
            if (__ajax) {//手机访问
                return Message.success("修改个人资料成功");
            }

            Message.success("保存用户信息成功");
        }

        return SUCCESS_MESSAGE;
    }



    /**
     * 用户头像显示编辑保存
     *
     * @param model
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "imageEdit")
    public String imageEdit(Model model) {
        User currentUser = UserUtils.getUser();
        if (currentUser != null) {
            model.addAttribute("user", currentUser);
            model.addAttribute("Global", Global.getInstance());

        }
        return "modules/sys/userImageEdit";
    }

    /**
     * 用户头像显示编辑保存
     *
     * @param request
     * @param file
     * @return
     * @throws IOException
     * @throws IllegalStateException
     */
    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "imageUpload")
    public Message imageUpload(HttpServletRequest request, MultipartFile file) throws IllegalStateException, IOException {
        User currentUser = UserUtils.getUser();

        // 判断文件是否为空
        if (!file.isEmpty()) {
            // 文件保存路径
            String realPath = Global.USERFILES
                    + UserUtils.getPrincipal().getLoginName() + "/images/";
            // 转存文件
            FileUtils.createDirectory(Global.getUserfilesBaseDir() + realPath);
            file.transferTo(new File(Global.getUserfilesBaseDir() + realPath + file.getOriginalFilename()));
            currentUser.setPhoto(request.getContextPath() + realPath + file.getOriginalFilename());
            systemService.updateUserInfo(currentUser);
        }

        return SUCCESS_MESSAGE;
    }

    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "uploadPhotoByBASE64")
    public Message uploadPhotoByWebCam(HttpServletRequest req) {
        User currentUser = UserUtils.getUser();
        String realPath = Global.USERFILES
                + UserUtils.getPrincipal().getLoginName() + "/images/";

        String filePath = req.getSession().getServletContext().getRealPath("/") + realPath;
        String fileName = DateUtils.getDate("yyyyMMddHHmmss") + ".png";
        //默认传入的参数带类型等参数：data:image/png;base64,
        String imgStr = req.getParameter("image");
        if (null != imgStr) {
            imgStr = imgStr.substring(imgStr.indexOf(",") + 1);
        }
        boolean generate = GenerateImage(imgStr, filePath, fileName);
        if (generate) {
            String result = realPath + fileName;
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            currentUser.setPhoto(request.getContextPath() + result);
            systemService.updateUserInfo(currentUser);
            return SUCCESS_MESSAGE;
        }
        return Message.error("上传异常");
    }

    /**
     * 功能描述：base64字符串转换成图片
     *
     * @since 2016/5/24
     */
    public boolean GenerateImage(String imgStr, String filePath, String fileName) {
        try {
            if (imgStr == null) {
                return false;
            }
            Base64 decoder = new Base64();
            //Base64解码
            byte[] b = decoder.decode(imgStr);

            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }

            //如果目录不存在，则创建
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            //生成图片
            OutputStream out = new FileOutputStream(filePath + fileName);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            logger.error("生成图片异常：{}", e.getMessage());
            return false;
        }


    }


    /**
     * 返回用户信息
     *
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "infoData")
    public Message infoData() {
        return Message.success("获取个人信息成功", UserUtils.getUser());
    }


    /**
     * 修改个人用户密码
     *
     * @param oldPassword
     * @param newPassword
     * @param model
     * @return
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "modifyPwd")
    public String modifyPwd(String oldPassword, String newPassword, Model model) {
        User user = UserUtils.getUser();
        if (StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)) {
            if (Global.isDemoMode()) {
                model.addAttribute("message", "演示模式，不允许操作！");
                return "modules/sys/userInfo";
            }
            if (systemService.validatePassword(oldPassword, user.getPassword())) {
                systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
                model.addAttribute("message", "修改密码成功");
            } else {
                model.addAttribute("message", "修改密码失败，旧密码错误");
            }
            return "modules/sys/userInfo";
        }
        model.addAttribute("user", user);
        return "modules/sys/userModifyPwd";
    }

    /**
     * 保存签名
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "saveSign")
    public Message saveSign(User user) {

        User currentUser = UserUtils.getUser();
        currentUser.setSign(user.getSign());
        systemService.updateUserInfo(currentUser);
        return Message.success("设置签名成功");
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String officeId) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<User> list = systemService.findUserByOfficeId(officeId);
        for (int i = 0; i < list.size(); i++) {
            User e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", "u_" + e.getId());
            map.put("pId", officeId);
            map.put("name", StringUtils.replace(e.getName(), " ", ""));
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * web端ajax验证用户名是否可用
     *
     * @param loginName
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "validateLoginName")
    public boolean validateLoginName(String loginName) {

        User user = userDao.findUniqueByProperty("login_name", loginName);
        if (user == null) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * web端ajax验证手机号是否可以注册（数据库中不存在）
     */
    @ResponseBody
    @RequestMapping(value = "validateMobile")
    public boolean validateMobile(String mobile) {
        User user = userDao.findUniqueByProperty("mobile", mobile);
        if (user == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * web端ajax验证手机号是否已经注册（数据库中已存在）
     */
    @ResponseBody
    @RequestMapping(value = "validateMobileExist")
    public boolean validateMobileExist(String mobile) {
        User user = userDao.findUniqueByProperty("mobile", mobile);
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    @ResponseBody
    @RequestMapping(value = "resetPassword")
    public Message resetPassword(String mobile) {
        if (userDao.findUniqueByProperty("mobile", mobile) == null) {
            return Message.error("手机号不存在");
        }
        User user = userDao.findUniqueByProperty("mobile", mobile);
        String newPassword = String.valueOf((int) (Math.random() * 900000 + 100000));
        systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
        return Message.success("密码重置成功!");
    }


    @ResponseBody
    @RequiresPermissions("user")
    @RequestMapping(value = "uploadPhoto")
    public Message imageUpload(
            @RequestParam(value = "x") String x,
            @RequestParam(value = "y") String y,
            @RequestParam(value = "h") String h,
            @RequestParam(value = "w") String w,
            @RequestParam(value = "file") MultipartFile file) throws IOException {
        User currentUser = UserUtils.getUser();

        // 判断文件是否为空
        if (!file.isEmpty()) {
            // 文件保存路径
            String realPath = Global.USERFILES
                    + UserUtils.getPrincipal() + "/images/";
            int imageX = Integer.parseInt(new BigDecimal(x).setScale(0, BigDecimal.ROUND_DOWN).toString());
            int imageY = Integer.parseInt(new BigDecimal(y).setScale(0, BigDecimal.ROUND_DOWN).toString());
            int imageH = Integer.parseInt(new BigDecimal(h).setScale(0, BigDecimal.ROUND_DOWN).toString());
            int imageW = Integer.parseInt(new BigDecimal(w).setScale(0, BigDecimal.ROUND_DOWN).toString());
            // 转存文件
            FileUtils.createDirectory(Global.getUserfilesBaseDir() + realPath);

            String original = Global.getUserfilesBaseDir() + realPath + file.getOriginalFilename();

            file.transferTo(new File(original));
            String thumbnail = imgCut(original, imageX, imageY, imageW, imageH);
            currentUser.setPhoto(thumbnail);
            systemService.updateUserInfo(currentUser);
        }
        return SUCCESS_MESSAGE;
    }


    /**
     * 截取图片
     *
     * @param src 原图片地址
     * @param x   截取时的x坐标
     * @param y   截取时的y坐标
     * @param w   截取的宽度
     * @param h   截取的高度
     */
    private static String imgCut(String src, int x, int y, int w,
                                 int h) throws IOException {
        String realPath = Global.USERFILES
                + UserUtils.getPrincipal() + "/images/";


        Image img;
        ImageFilter cropFilter;
        BufferedImage bi = ImageIO.read(new File(src));
        int srcWidth = bi.getWidth();
        int srcHeight = bi.getHeight();
        if (srcWidth >= w && srcHeight >= h) {
            Image image = bi.getScaledInstance(srcWidth, srcHeight, Image.SCALE_DEFAULT);
            cropFilter = new CropImageFilter(x, y, w, h);
            img = Toolkit.getDefaultToolkit().createImage(
                    new FilteredImageSource(image.getSource(), cropFilter));
            BufferedImage tag = new BufferedImage(w, h,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();

            //输出文件
            UUID uuid = UUID.randomUUID();

            String imagefile = Global.getUserfilesBaseDir() + realPath + uuid + "_cut.jpg";
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String result = request.getContextPath() + realPath + uuid + "_cut.jpg";
            ImageIO.write(tag, "JPEG", new File(imagefile));
            return result;
        }
        return null;
    }
}
