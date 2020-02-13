package com.dataguise.saas.service;

import com.dataguise.saas.dto.*;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.structures.CustomObject;
import com.dg.saas.orch.models.structures.DrillDownDetailedResponse;
import com.dg.saas.orch.models.structures.ErrorConstants;
import com.dg.saas.orch.models.structures.Modules;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import io.swagger.client.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The Overview service
 */
@Service
public class OverviewService {

    private static Logger logger = Logger.getLogger(OverviewService.class);

    @Autowired
    private DgSecureRestDriver restDriver;

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private ProtectionTaskService protectionTaskService;

    @Autowired
    SourceHierarchyExplorerService sourceHierarchyExplorerServiceService;

    /**
     * Gets Source Count of cloud and on premise sources.
     *
     * @return SourceCount
     */
    public ResponseEntity getSourceCount() throws BadGatewayException {
        SourceCountDTO sourceCount = new SourceCountDTO();
        Integer cloud = 0;
        Integer on_premise = 0;
        List<ConnectionDTO> connectionsFromController = connectionService.getConnections(Optional.empty());
        for (ConnectionDTO conn : connectionsFromController) {
            DgConnection dgConnection = conn.getConnection();
            if (dgConnection.getLocation().equalsIgnoreCase("Cloud")) {
                cloud++;
            } else {
                on_premise++;
            }
        }
        sourceCount.setCloud_sources(cloud);
        sourceCount.setOn_premise_sources(on_premise);
        return new ResponseEntity<>(sourceCount, HttpStatus.OK);

    }

    /**
     * Get all summary details from dashboard api for both cloud and on premise sources.
     *
     * @return List of summary details for cloud and on premise sources.
     */
    public List<SummaryDetailsDTO> getAllSummaryDetails() throws BadGatewayException, InternalServerException {
        Gson gson = new Gson();
        List<SummaryDetailsDTO> newSourceList = new ArrayList<>();
        List<CustomObject> sourceList = null;
        try {
            sourceList = restDriver.getAllSummaryDetails().getSourceList();
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        for (CustomObject sources : sourceList) {
            String name = sources.getName();
            String value = sources.getValue().toString();
            System.out.println("value is: " + value);
            try {
                SummaryDetailsDTO dto = gson.fromJson(value, SummaryDetailsDTO.class);
                dto.setName(name);
                newSourceList.add(dto);
            } catch (JsonSyntaxException e) {
                throw new InternalServerException(ErrorConstants.ERROR_PARSING_SOURCE_LIST.getErrorMessage());
            }
        }
        return newSourceList;
    }

    /**
     * Get dbms cloud sources from dashboard api using ConnectionHierarchyExplorer service.
     *
     * @return a source list response in formatted structure .
     */
    public List<SourceTreeViewUIFormatterDTO> getDashboardSources() throws BadGatewayException, InternalServerException {
        Gson gson = new Gson();
        List<SourcesDTO> sourcesDTOList = new ArrayList<>();
        List<CustomObject> sourceList = null;
        try {
            sourceList = restDriver.getConnections().getSourceList();
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        for (CustomObject sources : sourceList) {
            String name = sources.getName();
            String value = sources.getValue().toString();
            try {
                SourcesDTO sourceDTO = gson.fromJson(value, SourcesDTO.class);
                sourceDTO.setName(name);
                sourcesDTOList.add(sourceDTO);
            } catch (JsonSyntaxException e) {
                throw new InternalServerException(ErrorConstants.ERROR_PARSING_SOURCE_LIST.getErrorMessage());
            }
        }
        return sourceHierarchyExplorerServiceService.getFormattedOverviewSourceList(sourcesDTOList);
    }

    /**
     * Get dbms cloud databases of a selected source from dashboard api using ConnectionHierarchyExplorer service.
     *
     * @return a list of databases for a source in formatted structure.
     */
    public List<SourceTreeViewUIFormatterDTO> getDashboardDatabases(Integer connectionId) throws BadRequestException, BadGatewayException {
        List<SourceTreeViewUIFormatterDTO> formattedDatabaseList = null;
        if (connectionId == null) {
            throw new BadRequestException("Connection " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        try {
            List<DrillDownDetailedResponse> detailedResponseList = restDriver.getDatabasesForConnection(connectionId);
            formattedDatabaseList = sourceHierarchyExplorerServiceService.getFormattedOverviewDatabaseList(detailedResponseList, connectionId);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return formattedDatabaseList;
    }


    /**
     * Get all sources from controller using ConnectionHierarchyExplorer service.
     *
     * @return all source list response in formatted structure .
     */
    public List<SourceTreeViewUIFormatterDTO> getSources() throws BadGatewayException, InternalServerException {
        List<SourcesDTO> sourcesDTOList = new ArrayList<>();
        List<ConnectionDTO> dgConnectionList = connectionService.getConnections(Optional.empty());
        //Only detection sources
        dgConnectionList = dgConnectionList.stream().
                filter(dgConnection -> dgConnection.getConnection().getConnectionAgent().equalsIgnoreCase("D") || dgConnection.getConnection().getConnectionAgent().equalsIgnoreCase("M"))
                .collect(Collectors.toList());

        for (ConnectionDTO conn : dgConnectionList) {
            DgConnection dgConnection = conn.getConnection();
            SourcesDTO sourceDTO = new SourcesDTO();
            sourceDTO.setName(dgConnection.getConnectionName());
            sourceDTO.setSourceName(dgConnection.getConnectionName());
            sourceDTO.setSourceId(dgConnection.getConnectionId());
            sourceDTO.setServerType(dgConnection.getLocation());
            sourceDTO.setAgentType(dgConnection.getConnectionAgent());
            sourcesDTOList.add(sourceDTO);
        }
        List<SourceTreeViewUIFormatterDTO> ControllerSourceTreeViewUIFormatterDTOList =
                sourceHierarchyExplorerServiceService.getFormattedOverviewSourceList(sourcesDTOList);

        List<SourceTreeViewUIFormatterDTO> DashboardSourceTreeViewUIFormatterDTOList = getDashboardSources();

        return sourceHierarchyExplorerServiceService.getCategorizedOverviewList(
                ControllerSourceTreeViewUIFormatterDTOList,
                DashboardSourceTreeViewUIFormatterDTOList);
    }

    /**
     * Get dbms cloud hostname from controller api using DgSecureDriver service.
     *
     * @return a HostNamePortStruct list response in formatted structure .
     */
    public List<OverviewSourceDetail> getAllHost() throws BadGatewayException, InternalServerException, MalformedURLException {

        List<HostNamePortNumberStruct> hostNamePortNumberStructsList = new ArrayList<>();
        List<DgClusterInfo> clusterInfoList = new ArrayList<>();
        List<OverviewSourceDetail> hostNamePortNumberDTOList = new ArrayList<>();
        try {
            hostNamePortNumberStructsList = restDriver.getAllHost();
            for (HostNamePortNumberStruct hostNamePortNumberStruct : hostNamePortNumberStructsList) {
                OverviewSourceDetail overviewSourceDetail = new OverviewSourceDetail();
                String urlString = hostNamePortNumberStruct.getHostName();
                overviewSourceDetail.setLabel(hostNamePortNumberStruct.getHostName());
                if (urlString.matches("^(http|https|HTTP|HTTPS)://.*$")) {
                    URL url = new URL(hostNamePortNumberStruct.getHostName());
                    overviewSourceDetail.setLabel(url.getHost());
                }
                overviewSourceDetail.setSourceName(hostNamePortNumberStruct.getHostName());
                overviewSourceDetail.setPortNumber(hostNamePortNumberStruct.getPortNumber());
                overviewSourceDetail.setSourceType("rds");
                hostNamePortNumberDTOList.add(overviewSourceDetail);
            }
            clusterInfoList = restDriver.loadClustersInfo(0);
            for (DgClusterInfo dgClusterInfo : clusterInfoList) {
                OverviewSourceDetail overviewSourceDetail = new OverviewSourceDetail();
                overviewSourceDetail.setSourceName(dgClusterInfo.getClusterName());
                overviewSourceDetail.setLabel(dgClusterInfo.getClusterName());
                overviewSourceDetail.setSourceId(dgClusterInfo.getId());
                overviewSourceDetail.setSourceType("File");
                hostNamePortNumberDTOList.add(overviewSourceDetail);
            }

        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return hostNamePortNumberDTOList;
    }

    /**
     * Get all hostname databases from controller Services.
     *
     * @return all database info for hostname list response in formatted structure .
     */

    public List<OverviewSourceDetail> getAllDatabasesInfoForHost(HostNamePortNumberStruct hostNamePortNumberStruct) throws BadGatewayException, InternalServerException {

        List<String> databaseList = new ArrayList<>();
        List<OverviewSourceDetail> databaseTreeList = new ArrayList<>();
        try {
            databaseList = restDriver.getAllDatabasesInfoForHost(hostNamePortNumberStruct);
            for (String database : databaseList) {
                OverviewSourceDetail overviewSourceDetail = new OverviewSourceDetail();
                overviewSourceDetail.setLabel(database);
                overviewSourceDetail.setSourceType(Modules.RDS.name());
                databaseTreeList.add(overviewSourceDetail);
            }
            return databaseTreeList;
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }

    /**
     * Get dbms cloud tables for a database of a selected hostname from controller api.
     *
     * @return a  list of tables within a database .
     */

    public List<OverviewSourceDetail> getAllTablesInfoForHost(HostNamePortNumberStruct hostNamePortNumberStruct, String database) throws BadGatewayException, InternalServerException {

        List<String> tableList = new ArrayList<>();
        List<OverviewSourceDetail> tableTreeList = new ArrayList<>();
        try {
            tableList = restDriver.getAllTablesInfoForHost(hostNamePortNumberStruct, database);
            for (String table : tableList) {
                OverviewSourceDetail overviewSourceDetail = new OverviewSourceDetail();
                overviewSourceDetail.setLabel(table);
                overviewSourceDetail.setSourceType(Modules.RDS.name());
                tableTreeList.add(overviewSourceDetail);
            }
            return tableTreeList;

        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }

    /**
     * Get dbms cloud Columns Info for a database of a selected hostname from controller api.
     *
     * @return column details within a table .
     */

    public DgHostColumnsInfoWrapper getAllColumnsInfoForHost(HostNamePortNumberStruct hostNamePortNumberStruct, String database, String tableName) throws BadGatewayException, InternalServerException {

        DgHostColumnsInfoWrapper dgHostColumnsInfoWrapper = new DgHostColumnsInfoWrapper();
        try {
            dgHostColumnsInfoWrapper = restDriver.getAllColumnsInfoForHost(hostNamePortNumberStruct, database, tableName);
            return dgHostColumnsInfoWrapper;
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }

    }

    /**
     * Get lfaS3 cloud cluster result Info for a directories and files from controller api.
     *
     * @return column details within a table .
     */

    public DgS3OverviewResultsWrapper getS3OverviewResultsDirs(Integer clusterId, String dirFullPath) throws BadGatewayException, InternalServerException {
        DgS3OverviewResultsWrapper dgS3OverviewResultsWrapper = null;

        try {
            if(dirFullPath.equals("") || dirFullPath.isEmpty()) {
                dirFullPath+="/";
            } else if(dirFullPath.endsWith("/")){
               dirFullPath = dirFullPath.substring(0, dirFullPath.length()-1);
            }
            dgS3OverviewResultsWrapper = restDriver.getS3OverviewResultsDirs(clusterId, dirFullPath);

        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);

        }
        return dgS3OverviewResultsWrapper;
    }
}