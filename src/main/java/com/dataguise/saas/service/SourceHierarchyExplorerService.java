package com.dataguise.saas.service;

import com.dataguise.saas.dto.ColumnViewUIFormatterDTO;
import com.dataguise.saas.dto.ConnectionDTO;
import com.dataguise.saas.dto.SourceTreeViewUIFormatterDTO;
import com.dataguise.saas.dto.SourcesDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dg.saas.orch.models.structures.DrillDownDetailedResponse;
import io.swagger.client.model.DgConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * The Source Hierarchy service
 * Formatted and common structured responses to implement the source hierarchy calls on overview and scheduler page.
 */
@Service
public class SourceHierarchyExplorerService {

    @Autowired
    ConnectionService connectionService;

    /**
     * Get all sources on scheduler page in formatted structure.
     * @return List<SourceTreeViewUIFormatterDTO>
     */
    public List<SourceTreeViewUIFormatterDTO> getConnections(Optional<Integer> id) throws BadGatewayException {
        List<ConnectionDTO> connections = null;
        if (id.isPresent()) {
            connections = connectionService.getConnections(id);
        } else {
            connections = connectionService.getConnections(Optional.empty());
        }
        return getFormattedSchedulerSourceList(connections);
    }


    /**
     * Returns UI formatted list of sources for scheduler page at time of masking and detection.
     * @param dgConnectionList
     * @return List<SourceTreeViewUIFormatterDTO>
     */
    public List<SourceTreeViewUIFormatterDTO> getFormattedSchedulerSourceList(List<ConnectionDTO> dgConnectionList) {
        List<SourceTreeViewUIFormatterDTO> formattedSchedulerSourceList = new ArrayList<>();
        for(ConnectionDTO conn : dgConnectionList) {
            DgConnection dgConnection = conn.getConnection();
            SourceTreeViewUIFormatterDTO sourceTreeViewUIFormatterDTO = new SourceTreeViewUIFormatterDTO();
            sourceTreeViewUIFormatterDTO.setName(dgConnection.getConnectionName());
            sourceTreeViewUIFormatterDTO.setId(dgConnection.getConnectionId().toString());
            sourceTreeViewUIFormatterDTO.setParentId(null);
            sourceTreeViewUIFormatterDTO.setDetails(conn);
            formattedSchedulerSourceList.add(sourceTreeViewUIFormatterDTO);
        }
        return formattedSchedulerSourceList;
    }

    /**
     * Returns UI formatted list of sources for overview page.
     * @param sourcesDTOList
     * @return List<SourceTreeViewUIFormatterDTO>
     */
    public List<SourceTreeViewUIFormatterDTO> getFormattedOverviewSourceList(List<SourcesDTO> sourcesDTOList) {
        List<SourceTreeViewUIFormatterDTO> formattedOverviewSourceList = new ArrayList<>();
        for( SourcesDTO sourceDTO : sourcesDTOList) {
            SourceTreeViewUIFormatterDTO sourceTreeViewUIFormatterDTO = new SourceTreeViewUIFormatterDTO();
            sourceTreeViewUIFormatterDTO.setName(sourceDTO.getName());
            sourceTreeViewUIFormatterDTO.setId(sourceDTO.getSourceId().toString());
            sourceTreeViewUIFormatterDTO.setAgentType(sourceDTO.getAgentType());
            sourceTreeViewUIFormatterDTO.setCleaned((sourceDTO.getCleaned()!=null && sourceDTO.getCleaned()>0) ?true:false);
            sourceTreeViewUIFormatterDTO.setProtected((sourceDTO.getProtectedData()!=null && sourceDTO.getProtectedData()>0)?true:false);
            sourceTreeViewUIFormatterDTO.setSensitive((sourceDTO.getExposed()!=null && sourceDTO.getExposed()>0)?true:false);
            sourceTreeViewUIFormatterDTO.setUnscanned((sourceDTO.getUnscanned()!=null && sourceDTO.getUnscanned()>0)?true:false);
            sourceTreeViewUIFormatterDTO.setParentId(null);
// sourceTreeViewUIFormatterDTO.setDetails(sourceDTO);
            formattedOverviewSourceList.add(sourceTreeViewUIFormatterDTO);
        }
        return formattedOverviewSourceList;
    }


    /**
     * Returns UI formatted list of databases in a source for overview page.
     * @param responseList
     * @return List<SourceTreeViewUIFormatterDTO>
     */
    public List<SourceTreeViewUIFormatterDTO> getFormattedOverviewDatabaseList(
            List<DrillDownDetailedResponse> responseList,
            Integer connectionId ) {

        List<SourceTreeViewUIFormatterDTO> formattedOverviewDatabaseList = new ArrayList<>();
        for(DrillDownDetailedResponse response : responseList) {
            SourceTreeViewUIFormatterDTO sourceTreeViewUIFormatterDTO = new SourceTreeViewUIFormatterDTO();
            sourceTreeViewUIFormatterDTO.setName(response.getSourceName());
            sourceTreeViewUIFormatterDTO.setParentId(connectionId.toString());
            if(response.getExposed()!=0 || response.getProtectedData()!=0 || response.getCleaned()!=0) {
//Dashboard
                if(response.getCleaned()>0) {
                    sourceTreeViewUIFormatterDTO.setCleaned(true);
                } else {
                    sourceTreeViewUIFormatterDTO.setCleaned(false);
                }
                if(response.getProtectedData()>0) {
                    sourceTreeViewUIFormatterDTO.setProtected(true);
                } else {
                    sourceTreeViewUIFormatterDTO.setProtected(false);
                }
                if(response.getExposed()>0) {
                    sourceTreeViewUIFormatterDTO.setSensitive(true);
                } else {
                    sourceTreeViewUIFormatterDTO.setSensitive(false);
                }
                sourceTreeViewUIFormatterDTO.setId(response.getParentId().toString());
                sourceTreeViewUIFormatterDTO.setUnscanned(false);
            }
            else {
//Controller
                sourceTreeViewUIFormatterDTO.setId("-9999");
                sourceTreeViewUIFormatterDTO.setSensitive(false);
                sourceTreeViewUIFormatterDTO.setCleaned(false);
                sourceTreeViewUIFormatterDTO.setProtected(false);
                sourceTreeViewUIFormatterDTO.setUnscanned(true);
            }
//sourceTreeViewUIFormatterDTO.setDetails(response);
            formattedOverviewDatabaseList.add(sourceTreeViewUIFormatterDTO);
        }
        return formattedOverviewDatabaseList;
    }

    /**
     * Returns UI formatted list of tables for a database in a source for overview page.
     * @param responseList
     * @return List<SourceTreeViewUIFormatterDTO>
     */
    public List<SourceTreeViewUIFormatterDTO> getFormattedOverviewTableList(
            List<DrillDownDetailedResponse> responseList,
            Integer databaseId) {
        List<SourceTreeViewUIFormatterDTO> formattedOverviewTableList = new ArrayList<>();
        for(DrillDownDetailedResponse response : responseList) {
            SourceTreeViewUIFormatterDTO sourceTreeViewUIFormatterDTO = new SourceTreeViewUIFormatterDTO();
            sourceTreeViewUIFormatterDTO.setName(response.getSourceName());
            sourceTreeViewUIFormatterDTO.setParentId(databaseId.toString());
            if(response.getExposed()!=0 || response.getProtectedData()!=0 || response.getCleaned()!=0) {
//Dashboard
                if(response.getCleaned()>0) {
                    sourceTreeViewUIFormatterDTO.setCleaned(true);
                }else {
                    sourceTreeViewUIFormatterDTO.setCleaned(false);
                }
                if(response.getProtectedData()>0) {
                    sourceTreeViewUIFormatterDTO.setProtected(true);
                }else {
                    sourceTreeViewUIFormatterDTO.setProtected(false);
                }
                if(response.getExposed()>0) {
                    sourceTreeViewUIFormatterDTO.setSensitive(true);
                } else {
                    sourceTreeViewUIFormatterDTO.setSensitive(false);
                }
                sourceTreeViewUIFormatterDTO.setId(response.getHashVal());
                sourceTreeViewUIFormatterDTO.setUnscanned(false);
            }
//sourceTreeViewUIFormatterDTO.setDetails(response);
            formattedOverviewTableList.add(sourceTreeViewUIFormatterDTO);
        }
        return formattedOverviewTableList;
    }

    /**
     * Returns UI formatted list of columns for a database in a source for overview page.
     * @param responseList
     * @return List<SourceTreeViewUIFormatterDTO>
     */
    public List<ColumnViewUIFormatterDTO> getFormattedOverviewColumnsList(List<DrillDownDetailedResponse> responseList) {
        List<ColumnViewUIFormatterDTO> formattedOverviewColumnList = new ArrayList<>();
        for(DrillDownDetailedResponse response : responseList) {
            ColumnViewUIFormatterDTO columnViewUIFormatterDTO = new ColumnViewUIFormatterDTO();
            columnViewUIFormatterDTO.setColumnName(response.getSourceName());
//TODO: sourceType or contentType must represent col data type. Bug fix required.
//columnViewUIFormatterDTO.setDataType(response.getContentType());
            columnViewUIFormatterDTO.setRegexGroup(response.getRegexGroupName());
            columnViewUIFormatterDTO.setRegexId(Integer.parseInt(response.getRegexIds()));
            columnViewUIFormatterDTO.setConstraintType(null);
            formattedOverviewColumnList.add(columnViewUIFormatterDTO);
        }
        return formattedOverviewColumnList;
    }

    public <T> Set<T> convertListToSet(List<T> formattedOverviewList) {
        Set<T> formattedSet = new HashSet<>(formattedOverviewList);
        return formattedSet;
    }

    public <T> List<T> convertSetToList(Set<T> set) {
        List<T> formattedList = new ArrayList<>(set);
        return formattedList;
    }


    public <T> Set<T> getUnprocessedSet(List<T> controllerResult, List<T> dashboardResult) {
        Set<T> controllerResultSet = convertListToSet(controllerResult);
        Set<T> dashboardResultSet = convertListToSet(dashboardResult);
        controllerResultSet.removeAll(dashboardResultSet);
        return controllerResultSet;
    }

    public <T> List<T> getCategorizedOverviewList(List<T> controllerResult, List<T> dashboardResult) {
        if(controllerResult != null) {
            Set<T> unprocessedOverviewSet = getUnprocessedSet(controllerResult, dashboardResult);
            List<T> unprocessedOverviewList = convertSetToList(unprocessedOverviewSet);
            dashboardResult.addAll(unprocessedOverviewList);
        }

        return dashboardResult;
    }
}