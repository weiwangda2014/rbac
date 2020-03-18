/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
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
import com.wandun.modules.sys.entity.Dict;
import com.wandun.modules.sys.service.DictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 字典Controller
 *
 * @author 云南万盾科技有限公司
 * @version 2014-05-16
 */
@Slf4j
@Controller
@RequestMapping(value = "${adminPath}/sys/dict")
public class DictController extends BaseController {

    @Autowired
    private DictService dictService;

    @ModelAttribute
    public Dict get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return dictService.get(id);
        } else {
            return new Dict();
        }
    }

    @RequiresPermissions("sys:dict:list")
    @RequestMapping(value = {"list", ""})
    public String list(Dict dict, HttpServletRequest request, HttpServletResponse response, Model model) {
        List<String> typeList = dictService.findTypeList();
        model.addAttribute("typeList", typeList);
        Page<Dict> page = dictService.findPage(new Page<Dict>(request, response), dict);
        model.addAttribute("page", page);
        return "modules/sys/dictList";
    }

    @RequiresPermissions(value = {"sys:dict:view", "sys:dict:add", "sys:dict:edit"}, logical = Logical.OR)
    @RequestMapping(value = "form")
    public String form(Dict dict, Model model) {
        model.addAttribute("dict", dict);
        return "modules/sys/dictForm";
    }

    @ResponseBody
    @RequiresPermissions(value = {"sys:dict:add", "sys:dict:edit"}, logical = Logical.OR)
    @RequestMapping(value = "save")//@Valid
    public Message save(Dict dict) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        dictService.save(dict);
        return SUCCESS_MESSAGE;
    }

    @ResponseBody
    @RequiresPermissions("sys:dict:del")
    @RequestMapping(value = "delete")
    public Message delete(Dict dict) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        dictService.delete(dict);
        return SUCCESS_MESSAGE;
    }


    /**
     * 批量删除角色
     *
     * @return
     */
    @ResponseBody
    @RequiresPermissions("sys:role:del")
    @RequestMapping(value = "deleteAll", method = RequestMethod.POST)
    public Message deleteAll(String ids) {
        if (Global.isDemoMode()) {
            return Message.error("演示模式，不允许操作！");
        }
        List<String> idArray = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(ids);;
        dictService.deleteAll(idArray);
        return SUCCESS_MESSAGE;
    }


    @RequiresPermissions("user")
    @ResponseBody
    @RequestMapping(value = "treeData")
    public List<Map<String, Object>> treeData(@RequestParam(required = false) String type, HttpServletResponse response) {
        List<Map<String, Object>> mapList = Lists.newArrayList();
        Dict dict = new Dict();
        dict.setType(type);
        List<Dict> list = dictService.findList(dict);
        for (int i = 0; i < list.size(); i++) {
            Dict e = list.get(i);
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", e.getId());
            map.put("pId", e.getParentId());
            map.put("name", StringUtils.replace(e.getLabel(), " ", ""));
            mapList.add(map);
        }
        return mapList;
    }

    @ResponseBody
    @RequestMapping(value = "listData")
    public List<Dict> listData(@RequestParam(required = false) String type) {
        Dict dict = new Dict();
        dict.setType(type);
        return dictService.findList(dict);
    }

}
