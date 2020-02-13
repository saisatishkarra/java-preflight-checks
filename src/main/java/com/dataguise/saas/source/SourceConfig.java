package com.dataguise.saas.source;

import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.dataguise.saas.source")
public class SourceConfig {

    @Bean
    public ServiceLocatorFactoryBean serviceLocatorBean(){
        ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
        bean.setServiceLocatorInterface(SourceFactory.class);
        return bean;
    }
}
