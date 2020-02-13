package com.dataguise.saas.controllers;

import com.dataguise.saas.dto.ColumnViewUIFormatterDTO;
import com.dataguise.saas.dto.SensitiveExpressionDTO;
import com.dataguise.saas.dto.SourceTreeViewUIFormatterDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dataguise.saas.service.ProtectionTaskService;
import com.dataguise.saas.service.SourceHierarchyExplorerService;
import com.dataguise.saas.service.TaskBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@ConfigurationProperties(prefix = "dgsecure")
@PropertySource("classpath:application.properties")
@RequestMapping(value = "/api/dbms")

public class MetadataController {

    @Autowired
    ProtectionTaskService protectionTaskService;

    @Autowired
    SourceHierarchyExplorerService sourceHierarchyExplorerService;

    @Autowired TaskBuilderService taskBuilderService;


    /**
     * List detection and protection sources on scheduler page while creating a schedule in formatted structure.
     *
     * @param connectionId
     * @return
     * @throws BadGatewayException
     */
    @RequestMapping(value = {"/connections", "/connections/{connectionId}"}, method = RequestMethod.GET)
    public List<SourceTreeViewUIFormatterDTO> getConnections(@PathVariable Optional<Integer> connectionId) throws BadGatewayException {
        return sourceHierarchyExplorerService.getConnections(connectionId);
    }

    /**
     * List databases of protection source on scheduler page while creating a protection schedule in formatted structure.
     *
     * @param connectionId
     * @return
     * @throws BadGatewayException
     */
    @RequestMapping(value = "/protection/databases", method = RequestMethod.GET)
    public List<SourceTreeViewUIFormatterDTO> getDatabases(@RequestParam Integer connectionId) throws BadGatewayException, BadRequestException {
        return protectionTaskService.listAllDatabases(connectionId);
    }

    /**
     * List tables of protection source database on scheduler page while creating a protection schedule in formatted structure.
     *
     * @param connectionId
     * @return
     * @throws BadGatewayException
     */
    @RequestMapping(value = "/protection/tables", method = RequestMethod.GET)
    public List<SourceTreeViewUIFormatterDTO> getTables(
            @RequestParam("connectionId") Integer connectionId,
            @RequestParam("databaseName") String databaseName) throws BadGatewayException, BadRequestException {
        return protectionTaskService.listAllTables(connectionId, databaseName);
    }

    /**
     * List columns of database table for a protection source on scheduler page while creating a protection schedule in formatted structure.
     * @param connectionId
     * @param databaseName
     * @param tableName
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     */
    @RequestMapping(value = "/protection/columns", method = RequestMethod.GET)
    public List<ColumnViewUIFormatterDTO> getColumns(
            @RequestParam("connectionId") Integer connectionId,
            @RequestParam("databaseName") String databaseName,
            @RequestParam("tableName") String tableName) throws BadGatewayException, BadRequestException, InternalServerException {
        return protectionTaskService.listAllColumns(connectionId, databaseName, tableName);
    }

    @RequestMapping(value = {"/getSensitiveExpressionDetails", "/getSensitiveExpressionDetails/{regexId}"},method = RequestMethod.GET)
    public List<SensitiveExpressionDTO> getSensitiveExpressionDetails(@PathVariable Optional<Integer> regexId) throws BadGatewayException, IOException {
        return taskBuilderService.getSensitiveExpressionDetails(regexId);
    }

}
