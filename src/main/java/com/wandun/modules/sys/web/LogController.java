/**
 * Copyright &copy; 2015-2020 <a href="http://www.wandun.net/">云南万盾科技有限公司</a> All rights reserved.
 */
package com.wandun.modules.sys.web;

import com.google.common.base.Splitter;
import com.wandun.common.persistence.Page;
import com.wandun.common.web.BaseController;
import com.wandun.common.web.Message;
import com.wandun.modules.sys.entity.Log;
import com.wandun.modules.sys.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 日志Controller
 *
 * @author 云南万盾科技有限公司
 * @version 2013-6-2
 */
@Slf4j
@Controller
@RequestMapping(value = "${adminPath}/sys/log")
public class LogController extends BaseController {

    @Autowired
    private LogService logService;

    @RequiresPermissions("sys:log:list")
    @RequestMapping(value = {"list", ""})
    public String list(Log log, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<Log> page = logService.findPage(new Page<>(request, response), log);
        model.addAttribute("page", page);
        return "modules/sys/logList";
    }


    /**
     * 批量删除
     *
     * @return
     */
    @ResponseBody
    @RequiresPermissions("sys:log:del")
    @RequestMapping(value = "deleteAll", method = RequestMethod.POST)
    public Message deleteAll(String ids) {
        List<String> idArray = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(ids);
        logService.deleteAll(idArray);
        return SUCCESS_MESSAGE;
    }

    /**
     * 批量删除
     *
     * @return
     */
    @ResponseBody
    @RequiresPermissions("sys:log:del")
    @RequestMapping(value = "empty")
    public Message empty() {
        logService.empty();
        return Message.success("清空日志成功");
    }
}
