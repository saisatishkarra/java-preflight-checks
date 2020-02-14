package com.dataguise.saas.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class RequestCounter extends WebMvcConfigurerAdapter {

        @Autowired
        private CounterService counterService;

        @Autowired
        private MeterRegistry meterRegistry;

        private Counter counter;

        private String counterName = "http.request";

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            counter = Counter.builder(counterName).register(meterRegistry);

            registry.addInterceptor(new HandlerInterceptorAdapter() {


                @Override
                public void afterCompletion(HttpServletRequest request,
                                            HttpServletResponse response, Object handler, Exception ex)
                    throws Exception {
                    counterService.increment(counterName);
                    counter.increment();
                }
            });
        }
    }

