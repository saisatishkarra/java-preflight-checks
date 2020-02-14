package com.dataguise.saas.controllers;

import com.dataguise.saas.dto.DgSecureDetails;
import com.dataguise.saas.dto.PolicyType;
import com.dataguise.saas.dto.SettingsDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import io.swagger.client.model.DgPreferenceStruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/settings")
public class SettingsController {
    private static Logger logger = Logger.getLogger(SettingsController.class);

    @Autowired
    private DgSecureRestDriver driver;

    @RequestMapping(value = "/saveSettings", method = RequestMethod.POST)
    public void saveSettings(@RequestBody SettingsDTO settingsDTO) throws BadGatewayException, BadRequestException {
        StringBuffer scheduleTaskPolicyType = new StringBuffer();
        List<String> policyList  = settingsDTO.getPolicySelected();
        if (policyList.isEmpty() || policyList ==null) {
            throw new BadRequestException("Policy is not selected. Please select at-least one policy.", HttpStatus.BAD_REQUEST);
        }
        for (String policyType : settingsDTO.getPolicySelected()) {
            if (policyType.equalsIgnoreCase(PolicyType.HIPAA.name())
                    || policyType.equalsIgnoreCase(PolicyType.PCI.name())
                    || policyType.equalsIgnoreCase(PolicyType.PII.name())
                    || policyType.equalsIgnoreCase(PolicyType.GDPR.name())) {
                scheduleTaskPolicyType.append(policyType);
                scheduleTaskPolicyType.append(",");
            } else {
                scheduleTaskPolicyType = null;
                break;
            }
        }
        try {
                if(scheduleTaskPolicyType != null) {
                    scheduleTaskPolicyType = scheduleTaskPolicyType.deleteCharAt(scheduleTaskPolicyType.length() - 1);
                    driver.updateDefaultPolicy(scheduleTaskPolicyType.toString());
                }else {
                    throw new BadRequestException("Wrong policy value detected. Should be PCI, PII, HIPAA, GDPR", HttpStatus.BAD_REQUEST);
                }
            } catch (DgDrvException e) {
                logger.debug("Check if DgSecure version supports policy as preference.");
                throw new BadGatewayException("Policy value could not be updated as preference.", HttpStatus.BAD_GATEWAY);
            }
    }

    @RequestMapping(value = "/loadSettings", method = RequestMethod.GET)
    public SettingsDTO loadSettings() throws BadGatewayException {

        SettingsDTO settingsDTO = new SettingsDTO();
        List<String> policyList = new ArrayList<>();
        List<DgPreferenceStruct> policyPrefList = null;

        settingsDTO.setDgsecureClassicURL(getDgSecureURL());

        try {
            policyPrefList = driver.getDefaultPolicy();
        } catch (DgDrvException e) {
            throw new BadGatewayException("Unable to load policy value.", HttpStatus.BAD_GATEWAY);
        }
        if(!policyPrefList.isEmpty()) {
            String policy = policyPrefList.get(0).getPrefParamValue();
            if(policy.equals("") || policy == null) {
                throw new BadGatewayException("Policy value is not set.", HttpStatus.BAD_GATEWAY);
            }
            if(policy.contains(",")) {
                String[] policies = policy.split(",");
                for(String s : policies) {
                    policyList.add(s);
                }
            } else {
                policyList.add(policy);
            }
        } else {
            logger.debug("Empty policy pref list from DgSecure. Is SaaS.UI.Default_Policy supported by DgSecure version?");
        }
        settingsDTO.setPolicyType(policyList);

        return settingsDTO;
    }


    public String getDgSecureURL() {
        DgSecureDetails dgSecure = new DgSecureDetails();
        URL controllerBaseURL = null;
        try {
            controllerBaseURL = new URL(driver.getControllerClient().getApiClient().getBasePath());
        } catch (MalformedURLException e) {
            logger.error("Malformed DgSecure url.");
        }
        if(controllerBaseURL.getProtocol().equalsIgnoreCase("http")){
            dgSecure.setSecure(false);
        }else if(controllerBaseURL.getProtocol().equalsIgnoreCase("https")){
            dgSecure.setSecure(true);
        }
        dgSecure.setHost(controllerBaseURL.getHost());
        dgSecure.setPort(controllerBaseURL.getPort());
        dgSecure.setProtocol(controllerBaseURL.getProtocol());
        dgSecure.setDgsecureURL(String.format("%s://%s:%s/",controllerBaseURL.getProtocol(), controllerBaseURL.getHost(), controllerBaseURL.getPort()));
        return dgSecure.getDgsecureURL();
    }

    public String getJarPath(){
        String jarPath = SettingsController.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            jarPath = URLDecoder.decode(jarPath,"utf-8");
            jarPath = jarPath.substring(0, jarPath.indexOf("!"));
            jarPath = jarPath.substring(0,jarPath.lastIndexOf("/"));
            jarPath = jarPath.substring(jarPath.indexOf(":")+1,jarPath.length());
            logger.info("jarPath is: "+jarPath );
        } catch (UnsupportedEncodingException e) {
            logger.error("Unable to decode jar path to UTF-8");
        }
        return jarPath;
    }

}
