package org.nozomi.jikkenkichi.machikouba.fliter;


import org.nozomi.jikkenkichi.machikouba.pojo.LocalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Add an overall path prefix to the service to facilitate the use with ProcessFilter
 */
@Configuration
public class PathPrefixConfig implements WebMvcConfigurer {
    @Autowired
    LocalConfig localConfig;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(localConfig.getPathPrefix(), c -> c.isAnnotationPresent(RestMappingController.class));
    }
}