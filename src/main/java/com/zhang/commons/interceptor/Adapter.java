package com.zhang.commons.interceptor;


import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class Adapter implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController( "/" ).setViewName( "index" ); // 这里之前setViewName( "forward:/index.shtml" )，但是好像spring boot2之后不行，只能用现在的方法
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
    }
}
