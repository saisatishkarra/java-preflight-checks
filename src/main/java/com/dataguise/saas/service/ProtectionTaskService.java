package com.dataguise.saas.service;

import com.dataguise.saas.controllers.SettingsController;
import com.dataguise.saas.dto.*;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.builders.MaskerTaskBuilder;
import com.dg.saas.orch.models.structures.DgMaskerStatus;
import com.dg.saas.orch.models.structures.ErrorConstants;
import com.dg.saas.orch.models.structures.Modules;
import io.swagger.client.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class ProtectionTaskService {

    private static Logger logger = Logger.getLogger(ProtectionTaskService.class);

    @Autowired
    private DgSecureRestDriver restDriver;

    @Autowired
    private SettingsController settingsController;

    @Autowired
    private TaskBuilderService taskBuilderService;

    /**
     * Create a Protection task
     * @param protectionTask
     * @return
     * @throws BadGatewayException
     */
    public Integer createTask(@RequestBody DgMaskerTaskDTO protectionTask, String sourceModule) throws BadGatewayException {
        Integer taskId = null;
        try {
            taskId = restDriver.createMaskerTask(protectionTask, sourceModule);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Protection task created with id: " + taskId);
        return taskId;
    }

    /**
     * Edit a protection task
     * @param id
     * @param protectionTask
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     */
    public Integer editTask(@PathVariable Integer id, @RequestBody DgMaskerTaskDTO protectionTask)
            throws BadGatewayException, BadRequestException {
        Integer taskId = null;
        if( id == null) {
            throw new BadRequestException("Task " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        try {
            taskId = restDriver.updateMaskerTask(protectionTask, id);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Protection task edited with id: " + taskId);
        return taskId;
    }

    /**
     * Execute a protection task.
     * @param taskId
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    public Integer executeTask(@PathVariable Integer taskId) throws BadRequestException, BadGatewayException {
        if( taskId == null) {
            throw new BadRequestException("Task " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        Integer taskInstanceId = null;
        try {
            taskInstanceId = restDriver.executeMaskerTask(taskId);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Protection task executed with instance id: " + taskInstanceId);
        return taskInstanceId;
    }

    /**
     * Remove a protection task.
     * @param id
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    public Boolean deleteTask(@PathVariable String id) throws BadRequestException, BadGatewayException {
        Boolean taskDeleteStatus = false;

        if( id == null) {
            throw new BadRequestException("Task " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        try {
            taskDeleteStatus = restDriver.deleteMaskerTask(id);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return taskDeleteStatus;
    }

    /**
     * Get the task details of a existing protection task.
     * @param id
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    public DgMaskerTaskWrapper getTaskDetails(@PathVariable Integer id) throws BadRequestException, BadGatewayException {
        DgMaskerTaskWrapper maskerTaskWrapper = null;

        if( id == null) {
            throw new BadRequestException("Task " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }

        try {
            maskerTaskWrapper = restDriver.getMaskerTaskDetails(id);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return maskerTaskWrapper;
    }

    /**
     * Get the status of a protection task instance.
     * @param taskInstanceId
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    public DgMaskerStatus getTaskInstanceStatus(@PathVariable Integer taskInstanceId) throws BadRequestException, BadGatewayException {
        DgMaskerStatus maskerStatus = null;

        if( taskInstanceId == null) {
            throw new BadRequestException("Task Instance " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }

        try {
            maskerStatus = restDriver.getMaskerTaskStatus(taskInstanceId);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return maskerStatus;
    }

    /**
     * Get the protection task instance results.
     * @param taskInstanceId
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
//    public List<DgMaskerResults> getTaskInstanceResults(@PathVariable Integer taskInstanceId) throws BadRequestException, BadGatewayException {
//        List<DgMaskerResults> maskerTaskResults = null;
//
//        if( taskInstanceId == null) {
//            throw new BadRequestException("Task instance id must be specified in path variable");
//        }
//
//        try {
//            maskerTaskResults = restDriver.getMaskerTaskResults(taskInstanceId);
//        } catch (DgDrvException e) {
//            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
//        }
//        return maskerTaskResults;
//    }

    /**
     * Cancel a protection task instance.
     * @param taskInstanceId
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    public Boolean cancelTask(@PathVariable Integer taskInstanceId) throws BadRequestException, BadGatewayException {
        Boolean taskCancelStatus = false;

        if( taskInstanceId == null) {
            throw new BadRequestException("Task Instance " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        try {
            taskCancelStatus = restDriver.cancelMaskerTask(taskInstanceId);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return taskCancelStatus;
    }

    /**
     * Return the database from masking api calls to be used on overview page at time of protection schedule
     * @param connectionId
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    public List<SourceTreeViewUIFormatterDTO> listAllDatabases(@RequestParam Integer connectionId) throws BadRequestException, BadGatewayException {
        List<SourceTreeViewUIFormatterDTO> databaseList = new ArrayList<>();

        if( connectionId == null) {
            throw new BadRequestException("Protection Connection "+ ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        try {
            List<String> dbList = restDriver.listDatabasesForMasking(Modules.RDS, connectionId);
            for(String s : dbList) {
                SourceTreeViewUIFormatterDTO sourceTreeViewUIFormatterDTO = new SourceTreeViewUIFormatterDTO();
                sourceTreeViewUIFormatterDTO.setName(s);
                sourceTreeViewUIFormatterDTO.setId(s);
                sourceTreeViewUIFormatterDTO.setParentId(connectionId.toString());
                sourceTreeViewUIFormatterDTO.setDetails(null);
                databaseList.add(sourceTreeViewUIFormatterDTO);
            }
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return databaseList;
    }

    /**
     * Return the Tables from masking api calls to be used on overview page at time of protection schedule.
     * @param connectionId
     * @param databaseName
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    public List<SourceTreeViewUIFormatterDTO> listAllTables(@RequestParam("connectionId") Integer connectionId,
                                                            @RequestParam("databaseName") String databaseName) throws BadRequestException, BadGatewayException {

        List<SourceTreeViewUIFormatterDTO> tableList=new ArrayList<>();
        DbmsTableListWrapper dbmsTableListWrapper = new DbmsTableListWrapper();


        if( connectionId ==  null) {
            throw new BadRequestException("Protection Connection "+ ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }

        if(databaseName == null || databaseName == "") {
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_DATABASE.getErrorMessage());
        }
        try {
            dbmsTableListWrapper =  restDriver.listTablesForMasking(Modules.RDS, databaseName, connectionId);

            List<DbmsTableInfoBean> dbmsTableInfoBeans=dbmsTableListWrapper.getTableList();
            for(DbmsTableInfoBean s : dbmsTableInfoBeans) {
                SourceTreeViewUIFormatterDTO sourceTreeViewUIFormatterDTO = new SourceTreeViewUIFormatterDTO();
                sourceTreeViewUIFormatterDTO.setName(s.getTableName());
                sourceTreeViewUIFormatterDTO.setParentId(databaseName);
                sourceTreeViewUIFormatterDTO.setDetails(null);
                tableList.add(sourceTreeViewUIFormatterDTO);
           }
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return tableList;
    }

    /**
     * Return the Columns from masking api calls to be used on overview page at time of protection schedule.
     * @param connectionId
     * @param databaseName
     * @param tableName
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    public List<ColumnViewUIFormatterDTO> listAllColumns(
            @RequestParam("connectionId") Integer connectionId,
            @RequestParam("databaseName") String databaseName,
            @RequestParam("tableName") String tableName)
            throws BadRequestException, BadGatewayException, InternalServerException {

        List<ColumnViewUIFormatterDTO> columnList = new ArrayList<>();
        if( connectionId ==  null) {
            throw new BadRequestException("Protection Connection "+ ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        try {
            List<DgColumnInfo> columnInfoList = restDriver.listColumnsForMasking(Modules.RDS,
                    databaseName,
                    tableName,
                    connectionId);

            for(DgColumnInfo columnInfo : columnInfoList) {

                ColumnViewUIFormatterDTO columnViewUIFormatterDTO = new ColumnViewUIFormatterDTO();
                columnViewUIFormatterDTO.setColumnName(columnInfo.getName());
                columnViewUIFormatterDTO.setDataType(columnInfo.getType());

                if (columnInfo.getConstraintType().isEmpty())
                    columnViewUIFormatterDTO.setConstraintType(null);
                else
                    columnViewUIFormatterDTO.setConstraintType(columnInfo.getConstraintType());

                /**Defaults to regexId from detection if found sensitive or 0 if not sensitive.
                regexId is 0 for non sensitive type. Populate the dropdown for sensitive types.**/
                columnViewUIFormatterDTO.setRegexId(columnInfo.getRegexId());

                if(columnInfo.getSensitivity().equals(1)) {
                    columnViewUIFormatterDTO.setIsSensitive(true);

                    //Setting regexGroupName and regexLabel for sensitive columns.
                    List<SensitiveExpressionDTO> sensitiveExpressions = null;
                    try {
                        sensitiveExpressions = taskBuilderService.getSensitiveExpressionDetails(
                                Optional.of(columnInfo.getRegexId())
                        );
                    } catch (IOException e) {
                        logger.error(e);
                        throw new InternalServerException(
                                ErrorConstants.ERROR_INTERNAL_ERROR_FETCHING_DETAILS.getErrorMessage(),
                                HttpStatus.INTERNAL_SERVER_ERROR
                        );
                    }
                    columnViewUIFormatterDTO.setRegexGroup(sensitiveExpressions.get(0).getRegexGroup());
                    columnViewUIFormatterDTO.setRegexLabel(sensitiveExpressions.get(0).getRegexLabel());
                } else {
                    columnViewUIFormatterDTO.setIsSensitive(false);
                }
                columnList.add(columnViewUIFormatterDTO);
            }
        } catch (DgDrvException e) {
            logger.error(e);
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return columnList;
    }

    /**
     * Fetch all the masking structures, filter on structureId.
     * @param structureId
     *
     * Get all structures with structureId = 0
     */
    private List<DBMSStructUI> getStructures(Integer structureId, String module) throws BadGatewayException {
        List<DBMSStructUI> structure = null;
        try {
            structure = restDriver.getMaskingStructureDetails(module, structureId);
        } catch (DgDrvException e) {
            logger.error(e);
            throw new BadGatewayException(ErrorConstants.ERROR_FETCHING_STRUCTURE.getErrorMessage(), HttpStatus.BAD_GATEWAY);
        }
        return structure;
    }

    /**
     * Fetch all the mappings, filter on database name and connectionId.
     */
    private List<DgMaskerDbmsStructureMapping> getMapping(String databaseName, Integer connectionId) throws BadGatewayException {
        List<DgMaskerDbmsStructureMapping> structureMappings = null;
        try {
            structureMappings = restDriver.getStructureMappingDetails();
            if(connectionId!= null && connectionId!=0 &&
                    !databaseName.isEmpty() && databaseName!= null
                    && structureMappings!=null) {
                structureMappings = structureMappings.stream().filter(mapping ->
                                mapping.getConnectionId().equals(connectionId) &&
                                mapping.getDbName().equalsIgnoreCase(databaseName)
                                ).collect(Collectors.toList());
            }
        } catch (DgDrvException e) {
            logger.error(e);
            throw new BadGatewayException(e.getMessage() , HttpStatus.BAD_GATEWAY);
        }
        return structureMappings;
    }

    /**
     * Create a structure with list of columns and their sensitive details.
     */
    private Integer createStructure(List<ProtectionColumnUIStruct> columnStructureList, String module) throws BadGatewayException  {
        Integer structureId = 0;
        DBMSStructUI structure = new DBMSStructUI();
        String structureName = taskBuilderService.constructStructureName();
        structure.setName(structureName);
        structure.setDescription(structureName);
        structure.setId(structureId);
        structure.setModuleType(module);
        for (ProtectionColumnUIStruct columnStructure : columnStructureList) {
            DBMSStructColumnUI  dbmsColumnStruct = new DBMSStructColumnUI();
            dbmsColumnStruct.setColumnName(columnStructure.getColumnName());
            dbmsColumnStruct.setMaskingId(columnStructure.getRegexId());
            dbmsColumnStruct.setTableName(columnStructure.getTableName());
            structure.addColInfoListItem(dbmsColumnStruct);
        }
        try {

            structureId = restDriver.createMaskingSructure(module,structure);
        } catch (DgDrvException e) {
            logger.error(e);
           throw new BadGatewayException(ErrorConstants.ERROR_CREATING_STRUCTURE.getErrorMessage(), HttpStatus.BAD_GATEWAY);
        }
        return structureId;
    }


    /**
     * Add db mappings to structure
     */
    private Boolean mapDatabaseToStructure(Integer structureId, Integer connectionId, String databaseName, String module) throws BadGatewayException {
        Boolean mappingStatus;
        try {
            mappingStatus = restDriver.createStructureMapping(module, structureId, connectionId, databaseName);
        } catch (DgDrvException e) {
            logger.error("Error mapping structureId: "+ structureId + " with database: " + databaseName
                    + " for connectionId: " + connectionId);
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return mappingStatus;
    }

    /**
     * update columns of a structure
     */
    private Integer updateStructure(Integer existingStructureId,
                                    List<ProtectionColumnUIStruct> columnStructureList,String module)
            throws BadGatewayException {
        Integer structureId;
        DBMSStructUI structure = getStructures(existingStructureId,module).get(0);
        for (ProtectionColumnUIStruct columnStructure : columnStructureList) {
            DBMSStructColumnUI  dbmsColumnStruct = new DBMSStructColumnUI();
            dbmsColumnStruct.setColumnName(columnStructure.getColumnName());
            dbmsColumnStruct.setMaskingId(columnStructure.getRegexId());
            dbmsColumnStruct.setTableName(columnStructure.getTableName());
            structure.addColInfoListItem(dbmsColumnStruct);
        }
        try {
            structureId = restDriver.updateMaskingSructure(module,structure);
        } catch (DgDrvException e) {
            logger.error(e);
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return structureId;
    }

    /**
     * delete database to structure mapping.
     */
    private Integer deleteDbToStructureMapping(Integer structureId, Integer mappingId, String module) throws BadGatewayException {
        try {

            mappingId = restDriver.deleteStructureMapping(module, structureId, mappingId);
        } catch (DgDrvException e) {
            logger.error(e);
            throw new BadGatewayException(ErrorConstants.ERROR_DELETING_MAPPING.getErrorMessage() + ": " + mappingId , HttpStatus.BAD_GATEWAY);
        }
        return mappingId;
    }

    /**
     * delete structure column information.
     * @param columnId
     * @param structureId
     *
     * deletes all columns if columnId is 0
     */
    private Integer deleteStructureColumnInfo(Integer columnId, Integer structureId, String module) throws BadGatewayException {
        try {

            columnId = restDriver.deleteStructureColumns(module, columnId, structureId);
        } catch (DgDrvException e) {
            logger.error(e);
            throw new BadGatewayException(e.getMessage() , HttpStatus.BAD_GATEWAY);
        }
        return columnId;
    }

    /**
     * delete a structure
     */
    private Integer deleteStructure(Integer structureId, String module) throws BadGatewayException {
        try {
            structureId = restDriver.deleteMaskingSructure(module, structureId);
        } catch (DgDrvException e) {
            logger.error(e);
            throw new BadGatewayException(ErrorConstants.ERROR_DELETING_STRUCTURE.getErrorMessage()+": "+ structureId , HttpStatus.BAD_GATEWAY);
        }
        return structureId;
    }

    /**
     * Construct the protectionTaskParams to create a masking task
     * @param taskParamsDTO
     * @return
     * @throws BadGatewayException
     * @throws InternalServerException
     */
    public DgMaskerTaskDTO constructProtectionTask(TaskParamsDTO taskParamsDTO, String sourceModule)
            throws InternalServerException, BadGatewayException {



        DgMaskerTaskDTO maskerTaskDTO = null;

        String taskDetail = taskBuilderService.constructTaskName(taskBuilderService.PROTECTION_TYPE);
        Integer connectionId = taskParamsDTO.getSourceId();
        String module=Modules.valueOf(sourceModule).getModuleType();  //Using connectionType for getting the exact module during task creation because for snowflake db its location is on 'Cloud' but its module is 'DBMS' instead of 'RDS'

        /** Always perform masking using structure option and skip invalid columns.**/
        MaskerTaskBuilder builder = new MaskerTaskBuilder()
                .withTaskName(taskDetail)
                .withTaskDescription(taskDetail)
                .withConnection(connectionId)
                .withUseStructure(true)
                .withSkipInvalidColumns(true);
        builder = taskBuilderService.addTaskBuilderPolicies(builder);

        List<ProtectionTaskParamsDTO> protectionTaskParamsDTOList =
                taskParamsDTO.getProtectionTaskParamsDTO();

        /** Create structures and db mappings for all database, tables, column per connection. **/
        for(ProtectionTaskParamsDTO protectionTaskParamsDTO : protectionTaskParamsDTOList) {
            Integer structureId;
            String databaseName = protectionTaskParamsDTO.getDatabaseName();
            builder.addDatabase(databaseName);
            List<ProtectionColumnUIStruct> columnStructureList =
                    protectionTaskParamsDTO.getColumnStructure();

            /**check if db mapping exists and return a mappingList.**/
            List<DgMaskerDbmsStructureMapping> structureMappings = null;
            try {
                structureMappings = getMapping(databaseName, connectionId);
            } catch (BadGatewayException e) {
                logger.error(e);
                throw new InternalServerException(ErrorConstants.ERROR_FETCHING_DATABASE_MAPPING.getErrorMessage()+ ": " + databaseName +
                        " and connectionId: " + connectionId, HttpStatus.INTERNAL_SERVER_ERROR);
            }

            if (structureMappings!=null && structureMappings.size() == 1) {
                /** already db mapping exists, fetch structure and update cols.**/
                DgMaskerDbmsStructureMapping mapping = structureMappings.get(0);
                Integer mappingId = mapping.getId();
                Integer existingStructureId = mapping.getStructureId();
                Integer columnId = 0;
                /** Delete all existing columns and update with structure new columns.**/
                try {
                    deleteStructureColumnInfo(columnId, existingStructureId,module);
                } catch (BadGatewayException e) {
                    logger.error(e);
                    throw new InternalServerException(ErrorConstants.ERROR_DELETING_COLUMN.getErrorMessage()+": " + columnId,
                            HttpStatus.INTERNAL_SERVER_ERROR);
                }
                logger.debug("Delete column info successfully for an existing structure.");
                try {
                    updateStructure(existingStructureId, columnStructureList, module);
                } catch (BadGatewayException e) {
                    logger.error(e);
                    throw new InternalServerException(ErrorConstants.ERROR_UPDATING_STRUCTURE.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                logger.debug("Updated column info successfully for an existing structure.");

            } else {

                /** db mapping doesn't exist. create structure and db mapping. **/
                try {
                    structureId = createStructure(columnStructureList,module);
                } catch (BadGatewayException e) {
                    logger.error(e);
                    throw new InternalServerException(
                            ErrorConstants.ERROR_CREATING_STRUCTURE.getErrorMessage()+" for database: " + databaseName, HttpStatus.INTERNAL_SERVER_ERROR
                    );
                }
                logger.debug("Created new structure successfully.");
                /** Structure successfully created above.**/
                if (structureId != null) {
                    try {
                        mapDatabaseToStructure(structureId, connectionId, databaseName, module);
                    } catch (BadGatewayException e) {
                        logger.error(e);
                        throw new InternalServerException(
                                ErrorConstants.ERROR_CREATING_MAPPING.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR
                        );
                    }
                }
                logger.debug("Created mapping for the structure successfully.");
                //TODO: Handle clean up of structure, related column info, db mapping in case of failure.
            }
        }
        try {
            maskerTaskDTO = builder.build();
        } catch (DgDrvException e) {
            logger.error(e);
            throw new InternalServerException(ErrorConstants.ERROR_CONSTRUCTING_PROTECTION_TASK.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return maskerTaskDTO;
    }

}

