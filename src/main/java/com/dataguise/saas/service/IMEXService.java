package com.dataguise.saas.service;

import com.dataguise.saas.controllers.OverviewController;
import com.dataguise.saas.dto.ImexDTO;
import com.dataguise.saas.dto.SourceTreeViewUIFormatterDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.InternalServerException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import io.swagger.client.model.DgConnection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The Import and Export for sensitive reports service
 */
@Service
public class IMEXService {
    private static Logger logger = Logger.getLogger(IMEXService.class);
    @Autowired
    private DgSecureRestDriver restDriver;
    /**
     * Returns a octet stream in pdf and csv format for overview sensitive data report
     */
    public ResponseEntity<Resource> exportSensitiveDataReport(ImexDTO imexDTO)
            throws BadGatewayException {

        ResponseEntity<Resource> exportResource;
        byte[] sensitiveDataFile;
        String reportType = imexDTO.getReportType();
        String FILENAME = "Detection_Report";
        try {
            sensitiveDataFile = restDriver.exportSensitiveResults(imexDTO.getSourceName(), reportType, imexDTO.getExportedColList());
            ByteArrayResource resource = new ByteArrayResource(sensitiveDataFile);
            exportResource = ResponseEntity.ok()
                    .contentLength(sensitiveDataFile.length)
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename="+FILENAME+"."+reportType)
                    .body(resource);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return exportResource;
    }
}
