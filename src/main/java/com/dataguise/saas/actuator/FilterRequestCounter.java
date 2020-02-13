package com.dataguise.saas.actuator;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Component;

import javax.servlet.*;


@Component
public class FilterRequestCounter implements Filter{

    org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger("Filter");

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private CounterService counterService;

    private Counter counter;

    private String counterName = "http.filter.request";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        counter = Counter.builder(counterName).register(meterRegistry);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws java.io.IOException, ServletException {

        counterService.increment(counterName);
        counter.increment();

        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
