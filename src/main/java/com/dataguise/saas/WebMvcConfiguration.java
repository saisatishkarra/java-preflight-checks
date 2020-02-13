package com.dataguise.saas;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/loginSSO").setViewName("forward:/index.html");
        registry.addViewController("/scheduler").setViewName("forward:/index.html");
        registry.addViewController("/allConnections").setViewName("forward:/index.html");
        registry.addViewController("/addNewConnections").setViewName("forward:/index.html");
        registry.addViewController("/overview").setViewName("forward:/index.html");
        registry.addViewController("/connection").setViewName("forward:/index.html");
    }

}
