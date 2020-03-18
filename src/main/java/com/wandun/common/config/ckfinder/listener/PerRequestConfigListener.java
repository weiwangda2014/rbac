package com.wandun.common.config.ckfinder.listener;

import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.event.GetConfigForRequestEvent;
import com.cksource.ckfinder.exception.InvalidRequestException;
import com.cksource.ckfinder.listener.Listener;
import com.wandun.common.config.Global;
import com.wandun.common.web.Servlets;
import com.wandun.modules.sys.security.SystemAuthorizingRealm;
import com.wandun.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Named
public class PerRequestConfigListener implements Listener<GetConfigForRequestEvent> {
    private static final Logger logger = LoggerFactory.getLogger(PerRequestConfigListener.class);

    public PerRequestConfigListener() {
    }

    @Override
    public void onApplicationEvent(GetConfigForRequestEvent event) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();

        if (principal == null) {
            throw new InvalidRequestException("principal is not set");
        }

        String username = principal.getLoginName();

        if (username == null) {
            throw new InvalidRequestException("Username is not set");
        }
        Map<String, Config.Backend> backendConfigs = event.getConfig().getBackends();
        for (Config.Backend backendConfig : backendConfigs.values()) {
            if (backendConfig.getAdapter().equals("local")) {
                String root = Global.getUserfilesBaseDir() + Global.USERFILES_BASE_URL + principal.getLoginName() + "/";

                backendConfig.setRoot(root);
                backendConfig.setBaseUrl(Servlets.getRequest().getContextPath() + Global.USERFILES_BASE_URL + principal.getLoginName() + "/");

                Path dirPath = Paths.get(root);

                if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
                    logger.info(String.format("CKFinder files root directory for backend \"%s\": %s", backendConfig.getName(), dirPath));
                } else {
                    logger.info(String.format("CKFinder files root directory for backend \"%s\" doesn't exist", backendConfig.getName()));

                    try {
                        Files.createDirectories(dirPath);
                        logger.info(String.format("Created CKFinder files root directory for backend \"%s\" under %s", backendConfig.getName(), dirPath));
                    } catch (IOException e) {
                        logger.error(String.format("Could not create CKFinder files root directory for backend \"%s\" under %s", backendConfig.getName(), dirPath));
                    }
                }
            }
        }
    }
}