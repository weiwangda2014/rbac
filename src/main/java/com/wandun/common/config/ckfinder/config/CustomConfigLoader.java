package com.wandun.common.config.ckfinder.config;

import com.cksource.ckfinder.config.Config;
import com.cksource.ckfinder.config.loader.ConfigLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Named;
import java.io.InputStream;

@Named
public class CustomConfigLoader implements ConfigLoader {
    private static ResourceLoader resourceLoader = new DefaultResourceLoader();

    @Override
    public Config loadConfig() throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        InputStream stream = resourceLoader.getResource("ckfinder.yml").getInputStream();
        return mapper.readValue(stream, CustomConfig.class);
    }
}