package com.etu.expensemanagement.config;

import com.etu.expensemanagement.security.DevIdentityInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    private final boolean devIdentityEnabled;
    private final String[] allowedOrigins;

    public WebConfig(@Value("${app.dev-identity.enabled:false}") boolean devIdentityEnabled,
                     @Value("${app.cors.allowed-origins}") String[] allowedOrigins) {
        this.devIdentityEnabled = devIdentityEnabled;
        this.allowedOrigins = allowedOrigins;
        if (devIdentityEnabled) {
            log.warn("TEMPORARY dev identity is ENABLED (X-Dev-Employee-Id header). This is not real authentication.");
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (devIdentityEnabled) {
            registry.addInterceptor(new DevIdentityInterceptor()).addPathPatterns("/api/**");
        }
    }
}
