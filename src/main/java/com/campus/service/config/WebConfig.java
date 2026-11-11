package com.campus.service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ============================================================
 * WEB CONFIG: WebConfig
 * ============================================================
 *
 * Configures Spring MVC web routing.
 * Forwards root path ("/") directly to "/index.html" so users
 * immediately see the Smart Campus Service Desk UI upon visiting
 * http://localhost:8080.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}