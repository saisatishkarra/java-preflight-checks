package com.dataguise.saas.controllers;

import com.dataguise.saas.dto.ImexDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.InternalServerException;
import com.dataguise.saas.service.IMEXService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/imex")
public class IMEXController {
    private static Logger logger = Logger.getLogger(IMEXController.class);

    /** The connection service. */
    @Autowired
    private IMEXService imexService;

    /**
     * Returns a octet stream binary data and allows to download dashboard result as pdf.
     */
    @RequestMapping(value = {"/"},
            produces=MediaType.APPLICATION_OCTET_STREAM_VALUE,
            method = RequestMethod.POST)
    public ResponseEntity<Resource> getSensitveResultsReport(@RequestBody ImexDTO imexDTO) throws BadGatewayException, InternalServerException {
        return imexService.exportSensitiveDataReport(imexDTO);
    }
}
