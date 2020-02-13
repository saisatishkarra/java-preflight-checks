package com.dataguise.saas;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DataOrientedSaasUiApplication {
    Logger logger = Logger.getLogger(DataOrientedSaasUiApplication.class);
	public static void main(String[] args) {
        SpringApplication.run(DataOrientedSaasUiApplication.class, args);
	}
}
