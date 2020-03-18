/**
 * Copyright &copy; 2015-2020 王玮 All rights reserved.
 */
package com.wandun.modules.sys.web;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wandun.common.config.Global;
import com.wandun.common.persistence.Page;
import com.wandun.common.utils.StringUtils;
import com.wandun.common.web.BaseController;
import com.wandun.common.web.Message;
import com.wandun.modules.sys.entity.Area;
import com.wandun.modules.sys.service.AreaService;
import com.wandun.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 区域Controller
 *
 * @author 云南万盾科技有限公司
 * @version 2013-5-15
 */
@Slf4j
@Controller
@RequestMapping(value = "${adminPath}/sys/area")
public class AreaController extends BaseController {

    @Autowired
    private AreaService areaService;

    @ModelAttribute("area")
    public Area get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return areaService.get(id);
        } else {
            return new Area();
        }
    }

    @RequiresPermissions("sys:area:list")
    @RequestMapping(value = {"list", ""})
    public String list(Model model) {
        List<Area> page = areaService.findRoot();
        model.addAttribute("page", page);
        return "modules/sys/areaList";
    }

    @RequiresPermissions(value = {"sys:area:view", "sys:area:add", "sys:area:edit"}, logical = Logical.OR)
    @RequestMapping(value = "form")
    public String form(Area area, Model model) {
        if (area.getParent() == null || area.getParent().getId() == null) {
            area.setParent(UserUtils.getUser().getOffice().getArea());
        } else {
            area.setParent(areaService.get(area.getParent().getId()));
        }
        model.addAttribute("area", area);
        return "modules/sys/areaForm";
    }

    @ResponseBody
    @RequiresPermissions(value = {"sys:area:add", "sys:area:edit"}, logical = Logical.OR)
    @RequestMapping(value = "save")
    public Message save(Area area, BindingResult bindingResult) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        Message errorMessage = BeanValidators(area, bindingResult);
        if (errorMessage != null) {
            return errorMessage;
        }
        areaService.save(area);
        return SUCCESS_MESSAGE;
    }

    @ResponseBody
    @RequiresPermissions("sys:area:del")
    @RequestMapping(value = "delete")
    public Message delete(Area area) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        areaService.delete(area);
        return SUCCESS_MESSAGE;
    }

    @ResponseBody
    @RequiresPermissions("sys:area:del")
    @RequestMapping(value = "deleteAll", method = RequestMethod.POST)
    public Message deleteAll(String ids) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        List<String> idArray = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(ids);
        areaService.deleteAll(idArray);
        return SUCCESS_MESSAGE;
    }

    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Area> list = areaService.findRoot();
        for (int i = 0; i < list.size(); i++) {
            Area e = list.get(i);
            if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getPath().indexOf("," + extId + ",") == -1)) {
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParent().getId());
                map.put("name", e.getName());
                mapList.add(map);
            }
        }
        return mapList;
    }


    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "data")
    public List<Area> data(@RequestParam(required = false) String parentid) {
        List<Area> list = areaService.findChildsByParentId(parentid);
        return list;
    }
}
