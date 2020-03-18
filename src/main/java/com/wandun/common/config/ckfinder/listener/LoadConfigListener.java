package com.wandun.common.config.ckfinder.listener;

import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.event.LoadConfigEvent;
import com.cksource.ckfinder.listener.Listener;
import com.wandun.common.config.Global;
import com.wandun.common.web.Servlets;
import com.wandun.modules.sys.security.SystemAuthorizingRealm;
import com.wandun.modules.sys.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Named;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * A listener that dynamically changes the location of user files on application start.
 * <p>
 * It simply replaces the "{user.dir}" placeholder defined in the backend.root
 * configuration with value obtained from "user.dir" system property.
 */
@Named
public class LoadConfigListener implements Listener<LoadConfigEvent> {
    private static Logger logger = LoggerFactory.getLogger(LoadConfigListener.class);

    @Override
    public void onApplicationEvent(LoadConfigEvent loadConfigEvent) {

        SystemAuthorizingRealm.Principal principal = (SystemAuthorizingRealm.Principal) UserUtils.getPrincipal();

/*        if (principal != null) {
            Config config = loadConfigEvent.getConfig();

            Map<String, Config.Backend> backendConfigs = config.getBackends();
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
        }*/
    }
}