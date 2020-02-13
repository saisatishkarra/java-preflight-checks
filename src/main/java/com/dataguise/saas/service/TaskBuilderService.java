package com.dataguise.saas.service;

import com.dataguise.saas.controllers.SettingsController;
import com.dataguise.saas.dto.PolicyType;
import com.dataguise.saas.dto.SensitiveExpressionDTO;
import com.dataguise.saas.dto.SettingsDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.InternalServerException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.builders.MaskerTaskBuilder;
import com.dg.saas.orch.models.structures.DetectionTask;
import com.dg.saas.orch.models.structures.ErrorConstants;
import com.dg.saas.orch.models.structures.LFADetectionTask;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class TaskBuilderService {

    private static Logger logger = Logger.getLogger(TaskBuilderService.class);

    public static final String DETECTION_TYPE = "Detection";

    public static final String PROTECTION_TYPE = "Masking";

    @Autowired
    private SettingsController settingsController;

    @Autowired
    private DgSecureRestDriver restDriver;

    public String constructTaskName(String taskType) {
        StringBuffer taskName = new StringBuffer();
        if(taskType.contentEquals(DETECTION_TYPE)) {
            taskName.append("dt_"+ getTimeStampForDgSecure());
        } else if (taskType.contentEquals(PROTECTION_TYPE)) {
            taskName.append("mt_" + getTimeStampForDgSecure());
        }
        return taskName.toString();
    }

    public String constructStructureName() {
        StringBuffer structureName = new StringBuffer();
        structureName.append("st_"+getTimeStampForDgSecure());
        return structureName.toString();
    }

    public <T,T1> T1 addTaskBuilderPolicies(T taskBuilder) throws BadGatewayException, InternalServerException {
        T1 builder = null;
        SettingsDTO settingsDTO = settingsController.loadSettings();
        List<String> policyList = settingsDTO.getPolicySelected();
        if( taskBuilder instanceof DetectionTask.DetectionTaskBuilder) {
            DetectionTask.DetectionTaskBuilder detectionTaskBuilder = ((DetectionTask.DetectionTaskBuilder) taskBuilder);
            for(String policyType : policyList) {
                if(policyType.equalsIgnoreCase(PolicyType.HIPAA.name())) {
                    detectionTaskBuilder.addPolicyIds(1);
                } else if (policyType.equalsIgnoreCase(PolicyType.PCI.name())) {
                    detectionTaskBuilder.addPolicyIds(2);
                } else if (policyType.equalsIgnoreCase(PolicyType.PII.name())) {
                    detectionTaskBuilder.addPolicyIds(3);
                } else if (policyType.equalsIgnoreCase(PolicyType.GDPR.name())) {
                    detectionTaskBuilder.addPolicyIds(8);
                } else {
                    throw new InternalServerException(ErrorConstants.INVALID_POLICY_TYPE.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            builder = (T1) detectionTaskBuilder;
        } else if( taskBuilder instanceof MaskerTaskBuilder) {
            MaskerTaskBuilder maskerTaskBuilder = ((MaskerTaskBuilder) taskBuilder);
            for(String policyType : policyList) {
                if(policyType.equalsIgnoreCase(PolicyType.HIPAA.name())) {
                    maskerTaskBuilder.addPolicy(1);
                } else if (policyType.equalsIgnoreCase(PolicyType.PCI.name())) {
                    maskerTaskBuilder.addPolicy(2);
                } else if (policyType.equalsIgnoreCase(PolicyType.PII.name())) {
                    maskerTaskBuilder.addPolicy(3);
                } else if (policyType.equalsIgnoreCase(PolicyType.GDPR.name())) {
                    maskerTaskBuilder.addPolicy(8);
                } else {
                    throw new InternalServerException(ErrorConstants.INVALID_POLICY_TYPE.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            builder = (T1) maskerTaskBuilder;
        } else if( taskBuilder instanceof LFADetectionTask.LFADetectionTaskBuilder) {
            LFADetectionTask.LFADetectionTaskBuilder LFADetectionTaskBuilder = ((LFADetectionTask.LFADetectionTaskBuilder) taskBuilder);
            for(String policyType : policyList) {
                if(policyType.equalsIgnoreCase(PolicyType.HIPAA.name())) {
                    LFADetectionTaskBuilder.addPolicyName("HIPAA_Hadoop");
                } else if (policyType.equalsIgnoreCase(PolicyType.PCI.name())) {
                    LFADetectionTaskBuilder.addPolicyName("PCI_Hadoop");
                } else if (policyType.equalsIgnoreCase(PolicyType.PII.name())) {
                    LFADetectionTaskBuilder.addPolicyName("PII_Hadoop");
                } else if (policyType.equalsIgnoreCase(PolicyType.GDPR.name())) {
                    LFADetectionTaskBuilder.addPolicyName("GDPR_Hadoop");
                } else {
                    throw new InternalServerException(ErrorConstants.INVALID_POLICY_TYPE.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            builder = (T1) LFADetectionTaskBuilder;
        }
        return builder;
    }
    public String getTimeStampForDgSecure() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(tz);
        String timeStamp = sdf.format(date);

        logger.info("TIME STAMP IS: " + timeStamp);
        return timeStamp;
    }


    public List<SensitiveExpressionDTO> getSensitiveExpressionDetails(Optional<Integer> regexId) throws BadGatewayException, IOException {
        List<SensitiveExpressionDTO> sensitiveExpressionList = null;
        try {
            String sensitiveExpressionDetails =  restDriver.getSensitiveExpressionDetails();
            ObjectMapper mapper = new ObjectMapper();

            TypeReference<List<SensitiveExpressionDTO>> mapType = new TypeReference<List<SensitiveExpressionDTO>>(){};
            sensitiveExpressionList = mapper.readValue(sensitiveExpressionDetails, mapType);

            if (regexId.isPresent()) {
                sensitiveExpressionList = sensitiveExpressionList.stream().
                        filter(sensitiveExpressionDTO -> sensitiveExpressionDTO.getRegexId().equals(regexId.get()))
                        .collect(Collectors.toList());
            }
        } catch (DgDrvException e) {
            logger.error("Error in getting sensitive details: "+e);
            throw new BadGatewayException(ErrorConstants.ERROR_FETCHING_SENSITIVE_TYPE.getErrorMessage(), HttpStatus.BAD_GATEWAY);
        }
        return sensitiveExpressionList;
    }
}
