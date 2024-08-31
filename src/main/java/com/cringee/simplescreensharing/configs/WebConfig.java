package com.cringee.simplescreensharing.configs;

import com.cringee.simplescreensharing.interseptors.HxRequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final HxRequestInterceptor hxRequestInterceptor;

    WebConfig(HxRequestInterceptor hxRequestInterceptor) {
        this.hxRequestInterceptor = hxRequestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(hxRequestInterceptor).addPathPatterns("/**");
    }
}
