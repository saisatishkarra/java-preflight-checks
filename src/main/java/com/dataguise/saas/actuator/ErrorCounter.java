package com.dataguise.saas.actuator;

import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;

@ControllerAdvice
public class ErrorCounter {

    @Autowired
    private CounterService counterService;

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter backendUIExceptionCounter;
    private Counter driverExceptionCounter;
    private Counter webAppExceptionCounter;
    private Counter uncheckedExceptionCounter;

    private String backendUIExceptionCounterIdentifier = "http.request.error.backendUI";
    private String driverExceptionCounterIdentifier = "http.request.error.driver";
    private String webAppExceptionCounterIdentifier = "http.request.error.webapp";
    private String uncheckedExceptionCounterIdentifier = "http.request.error.unchecked";



    @PostConstruct
    public void init(){
        backendUIExceptionCounter = Counter.builder(backendUIExceptionCounterIdentifier).register(meterRegistry);
        driverExceptionCounter = Counter.builder(driverExceptionCounterIdentifier).register(meterRegistry);
        webAppExceptionCounter = Counter.builder(webAppExceptionCounterIdentifier).register(meterRegistry);
        uncheckedExceptionCounter = Counter.builder(uncheckedExceptionCounterIdentifier).register(meterRegistry);
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseBody
    public String handleInternalError(InternalServerException e) throws InternalServerException {
        counterService.increment(backendUIExceptionCounterIdentifier);
        backendUIExceptionCounter.increment();
        throw e;
    }

    @ExceptionHandler(BadGatewayException.class)
    @ResponseBody
    public String handleInternalError(BadGatewayException e) throws BadGatewayException {
        counterService.increment(driverExceptionCounterIdentifier);
        driverExceptionCounter.increment();
        throw e;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseBody
    public String handleInternalError(BadRequestException e) throws BadRequestException {
        counterService.increment(webAppExceptionCounterIdentifier);
        webAppExceptionCounter.increment();
        throw e;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleInternalError(Exception e) throws Exception {
        counterService.increment(uncheckedExceptionCounterIdentifier);
        uncheckedExceptionCounter.increment();
        throw e;
    }


}
