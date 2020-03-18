package com.wandun.modules.sys.listener;

import com.wandun.common.config.Global;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.util.WebAppRootListener;

import javax.servlet.ServletContext;

public class WebAppInitialize implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {

        servletContext.addListener(new WebAppRootListener());

        String config = Global.getConfig("app.name");
        servletContext.setInitParameter("webAppRootKey", config);

        StringBuilder sb = new StringBuilder();
        sb.append("\r\n======================================================================\r\n");
        sb.append("\r\n    " + Global.getConfig("app.name") + "\r\n");
        sb.append("\r\n  初始化参数开始   \r\n");
        sb.append("\r\n  webAppRootKey:" + config + "   \r\n");


        sb.append("\r\n  webAppRootKey:----" + servletContext.getInitParameter("webAppRootKey") + "   \r\n");


        sb.append("\r\n  初始化参数结束   \r\n");
        sb.append("\r\n======================================================================\r\n");
        System.out.println(sb.toString());
    }
}