/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.modules.sys.web;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wandun.common.config.Global;
import com.wandun.common.utils.StringUtils;
import com.wandun.common.web.BaseController;
import com.wandun.common.web.Message;
import com.wandun.modules.sys.entity.Office;
import com.wandun.modules.sys.entity.User;
import com.wandun.modules.sys.service.OfficeService;
import com.wandun.modules.sys.utils.DictUtils;
import com.wandun.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 机构Controller
 *
 * @author WanDun
 * @version 2013-5-15
 */
@Slf4j
@Controller
@RequestMapping(value = "${adminPath}/sys/office")
public class OfficeController extends BaseController {

    @Autowired
    private OfficeService officeService;

    @ModelAttribute("office")
    public Office get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return officeService.get(id);
        } else {
            return new Office();
        }
    }

    @RequiresPermissions("sys:office:index")
    @RequestMapping(value = {""})
    public String index() {
        return "modules/sys/officeIndex";
    }

    @RequiresPermissions("sys:office:index")
    @RequestMapping(value = {"list"})
    public String list(Office office, Model model) {
        if (office == null || office.getPath() == null) {
            model.addAttribute("list", officeService.findList(false));
        } else {
            model.addAttribute("list", officeService.findList(office));
        }
        return "modules/sys/officeList";
    }

    @RequiresPermissions(value = {"sys:office:view", "sys:office:add", "sys:office:edit"}, logical = Logical.OR)
    @RequestMapping(value = "form")
    public String form(Office office, Model model) {
        User user = UserUtils.getUser();
        if (office.getParent() == null || office.getParent().getId() == null) {
            office.setParent(user.getOffice());
        }
        office.setParent(officeService.get(office.getParent().getId()));
        if (office.getArea() == null) {
            office.setArea(user.getOffice().getArea());
        }
        // 自动获取排序号
        if (StringUtils.isBlank(office.getId()) && office.getParent() != null) {
            int size = 0;
            List<Office> list = officeService.findAll();
            for (int i = 0; i < list.size(); i++) {
                Office e = list.get(i);
                if (e.getParent() != null && e.getParent().getId() != null
                        && e.getParent().getId().equals(office.getParent().getId())) {
                    size++;
                }
            }
            office.setCode(office.getParent().getCode() + StringUtils.leftPad(String.valueOf(size > 0 ? size + 1 : 1), 3, "0"));
        }
        model.addAttribute("office", office);
        return "modules/sys/officeForm";
    }

    @ResponseBody
    @RequiresPermissions(value = {"sys:office:add", "sys:office:edit"}, logical = Logical.OR)
    @RequestMapping(value = "save")
    public Message save(Office office, BindingResult bindingResult) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        Message errorMessage = BeanValidators(office, bindingResult);
        if (errorMessage != null) {
            return errorMessage;
        }
        officeService.save(office);

        if (office.getChildDeptList() != null) {
            Office childOffice = null;
            for (String id : office.getChildDeptList()) {
                childOffice = new Office();
                childOffice.setName(DictUtils.getDictLabel(id, "sys_office_common", "未知"));
                childOffice.setParent(office);
                childOffice.setArea(office.getArea());
                childOffice.setType("2");
                childOffice.setGrade(String.valueOf(Integer.valueOf(office.getGrade()) + 1));
                childOffice.setUseable(Global.YES);
                officeService.save(childOffice);
            }
        }
        return SUCCESS_MESSAGE;
    }

    @ResponseBody
    @RequiresPermissions("sys:office:del")
    @RequestMapping(value = "delete")
    public Message delete(Office office) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        officeService.delete(office);
        return SUCCESS_MESSAGE;
    }


    @ResponseBody
    @RequiresPermissions("sys:office:del")
    @RequestMapping(value = "deleteAll", method = RequestMethod.POST)
    public Message deleteAll(String ids) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        List<String> idArray = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(ids);
        officeService.deleteAll(idArray);
        return SUCCESS_MESSAGE;
    }

    /**
     * 获取机构JSON数据。
     *
     * @param extId    排除的ID
     * @param type     类型（1：公司；2：部门/小组/其它：3：用户）
     * @param grade    显示级别
     * @param response
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String type,
                                              @RequestParam(required = false) Long grade, @RequestParam(required = false) Boolean isAll, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Office> list = officeService.findList(isAll);
        for (int i = 0; i < list.size(); i++) {
            Office e = list.get(i);
            if ((StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getPath().indexOf("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals("1") ? type.equals(e.getType()) : true)))
                    && (grade == null || (grade != null && Integer.parseInt(e.getGrade()) <= grade.intValue()))
                    && Global.YES.equals(e.getUseable())) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParent().getId());
                map.put("pIds", e.getPath());
                map.put("name", e.getName());
                if (type != null && "3".equals(type)) {
                    map.put("isParent", true);
                }
                mapList.add(map);
            }
        }
        return mapList;
    }
}
