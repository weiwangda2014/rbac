package com.wandun.modules.monitor.web;


import com.wandun.common.utils.MyBeanUtils;
import com.wandun.common.utils.StringUtils;
import com.wandun.common.web.BaseController;
import com.wandun.common.web.Message;
import com.wandun.modules.monitor.entity.Monitor;
import com.wandun.modules.monitor.service.MonitorService;
import com.wandun.modules.monitor.utils.SystemInfo;
import com.wandun.modules.sys.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.hyperic.sigar.Sigar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


/**
 * 系统监控Controller
 *
 * @author liugf
 * @version 2016-02-07
 */
@Slf4j
@Controller
@RequestMapping(value = "${adminPath}/monitor")
public class MonitorController extends BaseController {
    @Autowired
    private MonitorService monitorService;
    @Autowired
    private SystemConfigService systemConfigService;

    @ModelAttribute
    public Monitor get(@RequestParam(required = false) String id) {
        Monitor entity = null;
        if (StringUtils.isNotBlank(id)) {
            entity = monitorService.get(id);
        }
        if (entity == null) {
            entity = new Monitor();
        }
        return entity;
    }

    @RequestMapping("info")
    public String info(Model model) {
        Monitor monitor = monitorService.get("1");
        model.addAttribute("cpu", monitor.getCpu());
        model.addAttribute("jvm", monitor.getJvm());
        model.addAttribute("ram", monitor.getRam());
        model.addAttribute("toEmail", monitor.getToEmail());
        return "modules/monitor/info";
    }

    @RequestMapping("monitor")
    public String monitor() {
        return "modules/monitor/monitor";
    }

    @RequestMapping("systemInfo")
    public String systemInfo(Model model) {
        model.addAttribute("systemInfo", SystemInfo.SystemProperty());
        return "modules/monitor/systemInfo";
    }

    @ResponseBody
    @RequestMapping("usage")
    public Map<String, Object> usage() {
        Map<String, Object> sigar = SystemInfo.usage(new Sigar());

        return sigar;
    }

    /**
     * 修改配置
     *
     * @param monitor
     * @return
     */
    @ResponseBody
    @RequestMapping("modifySetting")
    public Message save(Monitor monitor) {
        Monitor t = monitorService.get("1");
        try {
            monitor.setId("1");
            MyBeanUtils.copyBeanNotNull2Bean(monitor, t);
            monitorService.save(t);
        } catch (Exception e) {
            return Message.error("保存失败");
        }
        return SUCCESS_MESSAGE;
    }
}