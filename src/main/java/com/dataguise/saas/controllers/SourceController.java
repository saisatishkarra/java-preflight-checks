package com.dataguise.saas.controllers;

import com.dataguise.saas.dto.ConnectionDTO;
import com.dataguise.saas.dto.SourcesDetails;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dataguise.saas.service.ConnectionService;
import com.dataguise.saas.service.SourceService;
import com.dataguise.saas.source.SourceDTO;
import io.swagger.client.model.DatabaseMetadata;
import io.swagger.client.model.DgConnection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/sources")
public class SourceController {

    private static Logger logger = Logger.getLogger(SourceController.class);

    @Autowired
    private SourceService sourceService;

    @Autowired
    private ConnectionService connectionService;

    /**
     * This API is used for creating the source
     *
     * @param sourceDTO
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     * @throws InternalServerException
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public Integer save(@RequestBody SourceDTO sourceDTO) throws BadGatewayException, BadRequestException, InternalServerException {
        return sourceService.saveSource(sourceDTO);
    }

    /**
     * This API is used for editing the source
     *
     * @param sourceDTO
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Integer edit(@RequestBody SourceDTO sourceDTO, @PathVariable Integer id) throws BadGatewayException, BadRequestException, InternalServerException {
        return sourceService.editSource(sourceDTO, id);
    }

    /**
     * This API is used to get the details before editing a source
     *
     * @param sourceModule
     * @param id
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     * @throws InternalServerException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public SourceDTO get(@RequestParam("sourceModule") String sourceModule, @PathVariable Integer id) throws BadGatewayException, BadRequestException, InternalServerException {
        return sourceService.getSourceDetails(sourceModule, id);
    }

    /**
     * This API will return the list of all sources(DBMS+Cloud)
     *
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<SourcesDetails> getAll() throws BadGatewayException, BadRequestException {
        return sourceService.getAllSources();
    }

    /**
     * This API is used for testing the source is active or not
     *
     * @param id
     * @param sourceModule
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     */
    @RequestMapping(value = "/test/{id}", method = RequestMethod.GET)
    public Boolean testExisting(@PathVariable Integer id, @RequestParam("sourceModule") String sourceModule) throws BadGatewayException, BadRequestException, InternalServerException {
        return sourceService.testExistingSource(id, sourceModule);
    }

    /**
     * This API is used for deleting the source
     *
     * @param id
     * @param sourceModule
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     * @throws InternalServerException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Integer delete(@PathVariable Integer id, @RequestParam("sourceModule") String sourceModule) throws BadGatewayException, BadRequestException, InternalServerException {
        return sourceService.deleteSource(id, sourceModule);
    }


    /**
     * Fetch a connection metadata for a new connection.
     */
    @RequestMapping(value = "/metadata", method = RequestMethod.PUT)
    public List<DatabaseMetadata> getMetadata(@RequestBody DgConnection dgConnection) throws BadRequestException,
            BadGatewayException {
        return connectionService.fetchNewConnectionMetadata(dgConnection);
    }

    /**
     * Fetch a connection metadata for a existing connection.
     */
    @RequestMapping(value = "/metadata/{id}", method = RequestMethod.GET)
    public List<DatabaseMetadata> getMetadata(@PathVariable Integer id) throws BadRequestException,
            BadGatewayException {
        return connectionService.fetchExistingConnectionMetadata(id, null);
    }

    /**
     * Test a new connection.
     */
    @RequestMapping(value = "/test", method = RequestMethod.PUT)
    public Boolean test(@RequestBody ConnectionDTO connectionDTO) throws BadGatewayException, BadRequestException {
        return connectionService.testNewConnection(connectionDTO);
    }


}
