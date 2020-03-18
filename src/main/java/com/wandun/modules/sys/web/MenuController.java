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
import com.wandun.modules.sys.entity.Menu;
import com.wandun.modules.sys.service.MenuService;
import com.wandun.modules.sys.service.SystemService;
import com.wandun.modules.sys.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 菜单Controller
 *
 * @author 云南万盾科技有限公司
 * @version 2013-3-23
 */
@Slf4j
@Controller
@RequestMapping(value = "${adminPath}/sys/menu")
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private SystemService systemService;

    @ModelAttribute("menu")
    public Menu get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return systemService.getMenu(id);
        } else {
            return new Menu();
        }
    }

    @RequiresPermissions("sys:menu:list")
    @RequestMapping(value = {"list", ""})
    public String list(Model model) {
        List<Menu> list = Lists.newArrayList();
        List<Menu> sourcelist = systemService.findAllMenu();
        Menu.sortList(list, sourcelist, Menu.getRootId(), true);
        model.addAttribute("list", list);
        return "modules/sys/menuList";
    }

    @RequiresPermissions(value = {"sys:menu:view", "sys:menu:add", "sys:menu:edit"}, logical = Logical.OR)
    @RequestMapping(value = "form")
    public String form(Menu menu, Model model) {
        if (menu.getParent() == null || menu.getParent().getId() == null) {
            menu.setParent(new Menu(Menu.getRootId()));
        }
        menu.setParent(systemService.getMenu(menu.getParent().getId()));
        // 获取排序号，最末节点排序号+30
        if (StringUtils.isBlank(menu.getId())) {
            List<Menu> list = Lists.newArrayList();
            List<Menu> sourcelist = systemService.findAllMenu();
            Menu.sortList(list, sourcelist, menu.getParentId(), false);
            if (list.size() > 0) {
                menu.setSort(list.get(list.size() - 1).getSort() + 30);
            }
        }
        model.addAttribute("menu", menu);
        return "modules/sys/menuForm";
    }

    @ResponseBody
    @RequiresPermissions(value = {"sys:menu:add", "sys:menu:edit"}, logical = Logical.OR)
    @RequestMapping(value = "save")
    public Message save(Menu menu, BindingResult bindingResult) {
        if (!UserUtils.getUser().isAdmin()) {
            return Message.error("越权操作，只有超级管理员才能添加或修改数据！");
        }
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        Message errorMessage = BeanValidators(menu, bindingResult);
        if (errorMessage != null) {
            return errorMessage;
        }
        systemService.saveMenu(menu);
        return SUCCESS_MESSAGE;
    }

    @ResponseBody
    @RequiresPermissions("sys:menu:del")
    @RequestMapping(value = "delete")
    public Message delete(Menu menu) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        systemService.deleteMenu(menu);
        return SUCCESS_MESSAGE;
    }

    @ResponseBody
    @RequiresPermissions("sys:menu:del")
    @RequestMapping(value = "deleteAll", method = RequestMethod.POST)
    public Message deleteAll(String ids) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        List<String> idArray = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(ids);
        systemService.deleteMenus(idArray);
        return SUCCESS_MESSAGE;
    }

    @RequiresPermissions("user")
    @RequestMapping(value = "tree")
    public String tree() {
        return "modules/sys/menuTree";
    }

    @RequiresPermissions("user")
    @RequestMapping(value = "treeselect")
    public String treeselect(String parentId, Model model) {
        model.addAttribute("parentId", parentId);
        return "modules/sys/menuTreeselect";
    }

    /**
     * 批量修改菜单排序
     */

    @RequiresPermissions("sys:menu:updateSort")
    @RequestMapping(value = "updateSort")
    public String updateSort(String[] ids, Integer[] sorts, RedirectAttributes redirectAttributes) {
        if (Global.isDemoMode()) {
            addMessage(redirectAttributes, "演示模式，不允许操作！");
            return "redirect:" + adminPath + "/sys/menu/";
        }
        for (int i = 0; i < ids.length; i++) {
            Menu menu = new Menu(ids[i]);
            menu.setSort(sorts[i]);
            systemService.updateMenuSort(menu);
        }
        addMessage(redirectAttributes, "保存菜单排序成功!");
        return "redirect:" + adminPath + "/sys/menu/";
    }

    /**
     * isShowHide是否显示隐藏菜单
     *
     * @param extId
     * @param isShowHide
     * @param response
     * @return
     */
    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String isShowHide, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        List<Menu> list = systemService.findAllMenu();
        for (int i = 0; i < list.size(); i++) {
            Menu e = list.get(i);
            if (StringUtils.isBlank(extId) || (extId != null && !extId.equals(e.getId()) && e.getParentIds().indexOf("," + extId + ",") == -1)) {
                if (isShowHide != null && isShowHide.equals("0") && e.getIsShow().equals("0")) {
                    continue;
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("id", e.getId());
                map.put("pId", e.getParentId());
                map.put("name", e.getName());
                mapList.add(map);
            }
        }
        return mapList;
    }
}
