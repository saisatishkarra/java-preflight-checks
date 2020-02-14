package com.dataguise.saas.controllers;

import com.dataguise.saas.dto.*;
import com.dataguise.saas.exception.InternalServerException;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.service.OverviewService;
import io.swagger.client.model.DgHostColumnsInfoWrapper;
import io.swagger.client.model.DgS3OverviewResultsWrapper;
import io.swagger.client.model.HostNamePortNumberStruct;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/overview")
public class OverviewController {

    private static Logger logger = Logger.getLogger(OverviewController.class);

    /**
     * The Overview service.
     */
    @Autowired
    private OverviewService overviewService;



    /**
     * List all sources count on cloud and on premise.
     */
    @RequestMapping(value = {"/sources-count"}, method = RequestMethod.GET)
    public ResponseEntity getSourceCount() throws BadGatewayException {
        return overviewService.getSourceCount();
    }

    /**
     * List of All Summary details both cloud and on premise from dashboard.
     */
    @RequestMapping(value = {"/data-category-breakdown"}, method = RequestMethod.GET)
    public List<SummaryDetailsDTO> getAllSummaryDetails() throws BadGatewayException, InternalServerException {
        return overviewService.getAllSummaryDetails();
    }

    /**
     * List of DBMS cloud sources on overview page in formatted structure.
     */
    @RequestMapping(value = {"/sources"}, method = RequestMethod.GET)
    public  List<SourceTreeViewUIFormatterDTO> getSources() throws BadGatewayException, InternalServerException {
        return overviewService.getSources();
    }

 /**
     * List of all hostName of DBMS cloud source on overview page in formatted structure.
     */
    @RequestMapping(value = {"/host"})
    public  List<OverviewSourceDetail> getAllHost() throws BadGatewayException, InternalServerException, MalformedURLException {
        return overviewService.getAllHost();
    }

    /**
     * List of DBMS cloud source databases associated with hostname on overview page in formatted structure.
     */
    @RequestMapping(value = {"/host/databases"},method = RequestMethod.POST)
    public List<OverviewSourceDetail> getAllDatabasesInfoForHost (
            @RequestBody HostNamePortNumberStruct hostNamePortNumberStruct ) throws BadRequestException, BadGatewayException,InternalServerException {

        return overviewService.getAllDatabasesInfoForHost(hostNamePortNumberStruct);

    }
    /**
     * List of tables in a database associated with hostname databases for a DBMS cloud source on overview page in formatted structure.
     */


    @RequestMapping(value = {"/host/tables"},method = RequestMethod.POST)
    public List<OverviewSourceDetail> getAllTablesInfoForHost (
            @RequestBody HostNamePortNumberStruct hostNamePortNumberStruct,
            @RequestParam("databaseName") String databaseName ) throws BadRequestException, BadGatewayException, InternalServerException{
        return overviewService.getAllTablesInfoForHost(hostNamePortNumberStruct,databaseName);
    }


    /**
     * List of tables in a database associated with hostname databases for a DBMS cloud source on overview page in formatted structure.
     */

    @RequestMapping(value = {"/host/tables/columns"},method = RequestMethod.POST)
    public DgHostColumnsInfoWrapper getAllColumnsInfoForHost (
            @RequestBody HostNamePortNumberStruct hostNamePortNumberStruct,
            @RequestParam("databaseName") String databaseName,
            @RequestParam("tableName") String tableName) throws BadRequestException, BadGatewayException, InternalServerException {
        return overviewService.getAllColumnsInfoForHost(hostNamePortNumberStruct,databaseName,tableName);
    }



    /**
     * List of all directories and files associated with the cluster for a LFA cloud source on overview page in formatted structure.
     */


    @RequestMapping(value = {"/results/s3lfa"},method = RequestMethod.GET)
    public DgS3OverviewResultsWrapper getS3OverviewResultsDirs (
            @RequestParam("clusterId") Integer clusterId,
            @RequestParam(value = "dirFullPath") String dirFullPath) throws BadRequestException, BadGatewayException, InternalServerException {

        return overviewService.getS3OverviewResultsDirs(clusterId, dirFullPath);

    }


}
