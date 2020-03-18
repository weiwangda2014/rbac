/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.modules.sys.web;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.wandun.common.config.Global;
import com.wandun.common.persistence.Page;
import com.wandun.common.utils.DateUtils;
import com.wandun.common.utils.MyBeanUtils;
import com.wandun.common.utils.StringUtils;
import com.wandun.common.utils.excel.ExportExcel;
import com.wandun.common.utils.excel.ImportExcel;
import com.wandun.common.web.BaseController;
import com.wandun.common.web.Message;
import com.wandun.modules.sys.entity.SystemConfig;
import com.wandun.modules.sys.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统配置Controller
 *
 * @author liugf
 * @version 2016-02-07
 */
@Slf4j
@Controller
@RequestMapping(value = "${adminPath}/sys/systemConfig")
public class SystemConfigController extends BaseController {

    @Autowired
    private SystemConfigService systemConfigService;

    @ModelAttribute
    public SystemConfig get(@RequestParam(required = false) String id) {
        SystemConfig entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = systemConfigService.get(id);
        }
        if (entity == null) {
            entity = new SystemConfig();
        }
        return entity;
    }

    /**
     * 系统配置列表页面
     */
    @RequiresPermissions("sys:systemConfig:index")
    @RequestMapping(value = {"index", ""})
    public String index(HttpServletRequest request, HttpServletResponse response, Model model) {
        SystemConfig systemConfig = systemConfigService.get("1");
        model.addAttribute("systemConfig", systemConfig);
        return "modules/sys/systemConfig";
    }


    /**
     * 保存系统配置
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "save")
    public Message save(SystemConfig systemConfig, Model model, RedirectAttributes redirectAttributes) {
        SystemConfig t = systemConfigService.get("1");
        try {
            systemConfig.setId("1");
            MyBeanUtils.copyBeanNotNull2Bean(systemConfig, t);
            systemConfigService.save(t);
        } catch (Exception e) {
            Message.error("保存系统配置失败");
        }
        return Message.success("保存系统配置成功");
    }

    /**
     * 删除系统配置
     *
     * @return
     */
    @ResponseBody
    @RequiresPermissions("sys:systemConfig:del")
    @RequestMapping(value = "delete")
    public Message delete(SystemConfig systemConfig) {
        systemConfigService.delete(systemConfig);
        return SUCCESS_MESSAGE;
    }

    /**
     * 批量删除系统配置
     *
     * @return
     */
    @ResponseBody
    @RequiresPermissions("sys:systemConfig:del")
    @RequestMapping(value = "deleteAll", method = RequestMethod.POST)
    public Message deleteAll(String ids) {
        List<String> idArray = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(ids);
        systemConfigService.deleteAll(idArray);
        return SUCCESS_MESSAGE;
    }

    /**
     * 导出excel文件
     */
    @RequiresPermissions("sys:systemConfig:export")
    @RequestMapping(value = "export", method = RequestMethod.POST)
    public String exportFile(SystemConfig systemConfig, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "系统配置" + DateUtils.getDate("yyyyMMddHHmmss") + ".xlsx";
            Page<SystemConfig> page = systemConfigService.findPage(new Page<SystemConfig>(request, response, -1), systemConfig);
            new ExportExcel("系统配置", SystemConfig.class).setDataList(page.getList()).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导出系统配置记录失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/sys/systemConfig/?repage";
    }

    /**
     * 导入Excel数据
     */
    @RequiresPermissions("sys:systemConfig:import")
    @RequestMapping(value = "import", method = RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            int successNum = 0;
            ImportExcel ei = new ImportExcel(file, 1, 0);
            List<SystemConfig> list = ei.getDataList(SystemConfig.class);
            for (SystemConfig systemConfig : list) {
                systemConfigService.save(systemConfig);
            }
            addMessage(redirectAttributes, "已成功导入 " + successNum + " 条系统配置记录");
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入系统配置失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/sys/systemConfig/?repage";
    }

    /**
     * 下载导入系统配置数据模板
     */
    @RequiresPermissions("sys:systemConfig:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
        try {
            String fileName = "系统配置数据导入模板.xlsx";
            List<SystemConfig> list = Lists.newArrayList();
            new ExportExcel("系统配置数据", SystemConfig.class, 1).setDataList(list).write(response, fileName).dispose();
            return null;
        } catch (Exception e) {
            addMessage(redirectAttributes, "导入模板下载失败！失败信息：" + e.getMessage());
        }
        return "redirect:" + Global.getAdminPath() + "/sys/systemConfig/?repage";
    }


}