package com.dataguise.saas.controllers;

import com.dataguise.saas.dto.AboutDTO;
import com.dataguise.saas.dto.DgSecureDetails;
import com.dg.saas.orch.client.DgSecureRestDriver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Manifest;

@RestController
@RequestMapping(value = "/api/about")
@CrossOrigin
public class AboutController {
    Logger logger = Logger.getLogger(AboutController.class);
    @Autowired private DgSecureRestDriver driver;

    @RequestMapping("/")
    public AboutDTO about() {
        AboutDTO about = new AboutDTO();
        try {
            Class theClass = AboutController.class;
            String classPath = theClass.getResource(theClass.getSimpleName() + ".class").toString();
            String libPath = classPath.substring(0, classPath.indexOf("!"));
            String filePath = libPath + "!/META-INF/MANIFEST.MF";
            logger.info("File:  " + filePath);
            Manifest manifest = new Manifest(new URL(filePath).openStream());
            about.setRevision(manifest.getMainAttributes().getValue("Revision"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String driverConfig = driver.toString();
        about.setDriverConfig(driverConfig);
        logger.debug(driverConfig);
        return about;
    }
}
