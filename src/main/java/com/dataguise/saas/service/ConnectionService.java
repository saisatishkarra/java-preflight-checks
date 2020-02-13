package com.dataguise.saas.service;

import com.dataguise.saas.dto.*;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.structures.Connection;
import com.dg.saas.orch.models.structures.ErrorConstants;
import io.swagger.client.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The Connection service
 */
@Service
public class ConnectionService {

    private static Logger logger = Logger.getLogger(ConnectionService.class);

    @Value("${dgsecure.scheduleScanType}")
    private String scanType;

    @Value("${dgsecure.scheduleStartDelayInMins}")
    private Integer delayInMins;

    @Autowired
    private DgSecureRestDriver restDriver;

    @Autowired
    private SchedulerService schedulerService;

    @Autowired
    private TaskBuilderService taskBuilderService;

    @Autowired
    private AgentService agentService;


    /**
     * Gets connections for both detection and protection in a flat structure.
     *
     * @return List of all connections in flat structure.
     */
    public List<ConnectionDTO> getConnections(Optional<Integer> id) throws BadGatewayException {
        List<DgConnection> dgConnections;
        List<ConnectionDTO> connectionsUIFormatter = new ArrayList<>();
        try {
            dgConnections = restDriver.getAllConnections();
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        if (id.isPresent()) {
            dgConnections = dgConnections.stream().
                filter(dgConnection -> dgConnection.getConnectionId().equals(id.get()))
                .collect(Collectors.toList());
        }
        if (dgConnections != null && !dgConnections.isEmpty()) {
            for (DgConnection dgConnection : dgConnections) {
                ConnectionDTO connection = new ConnectionDTO();
                Integer groupId = dgConnection.getGroupId();
                List<DgInfraStructureNodeWrapper> agentDetails = agentService
                    .filterAgentListByGroupId(groupId);
                DgInfraStructureNodeWrapper agent = null;
                if (agentDetails != null && !agentDetails.isEmpty()) {
                    agent = agentDetails.get(0);
                    connection.setAgentHostName(agent.getHostName());
                    connection.setAgentName(agent.getAgentName());
                    connection.setConnection(dgConnection);
                    connection.setAutoSchedule(null);
                    connectionsUIFormatter.add(connection);
                } else {
                    logger.warn("Unable to fetch agent details for connectionId: " + dgConnection
                        .getConnectionId());
                }

            }
        }

        connectionsUIFormatter.sort((obj1, obj2) -> obj2.getConnection().getConnectionId()
            .compareTo(obj1.getConnection().getConnectionId()));
        return connectionsUIFormatter;
    }

    /**
     * Add a connection.
     *
     * @return connectionId
     */
    public Integer addConnection(ConnectionDTO connectionDTO)
        throws BadRequestException, BadGatewayException, InternalServerException {
        DgConnection conn = connectionDTO.getConnection();
        Boolean defaultSchedule = connectionDTO.getAutoSchedule();
        String agentHostName = connectionDTO.getAgentHostName();
        String agentName = connectionDTO.getAgentName();

        String connectionAgentType = conn.getConnectionAgent();
        Integer connectionId = null;
        if (connectionAgentType == null) {
            throw new BadRequestException("Invalid Connection Agent type");
        }
        if (connectionAgentType
            .equalsIgnoreCase(Connection.ConnectionType.Discover.getConnectionType())) {
            List<String> schemaFilters = conn.getSchemaFilter();
            Iterator<String> itr = schemaFilters.iterator();
            while (itr.hasNext()) {
                String schema = itr.next();
                if (schema.equals("")) {
                    throw new BadRequestException(
                        ErrorConstants.ERROR_VALIDATING_SCHEMA.getErrorMessage(),
                        HttpStatus.BAD_REQUEST);
                }
            }
            if (conn.getSchemaFilter().size() == 0) {
                throw new BadRequestException(
                    ErrorConstants.ERROR_VALIDATING_SCHEMA_FILTER.getErrorMessage(),
                    HttpStatus.BAD_REQUEST);
            }

            if (conn.getGroupId() == null || conn.getGroupId() == 0) {
                //Make sure UI sets group Id using getAgentDetails call.
                throw new BadRequestException(
                    ErrorConstants.ERROR_VALIDATING_GROUP_ID.getErrorMessage());
            }

            if (agentHostName == null || agentHostName.isEmpty()) {
                //Make sure UI sets agentHostName using getAgentDetails call.
                throw new BadRequestException(
                    ErrorConstants.ERROR_VALIDATING_AGENT_HOSTNAME.getErrorMessage());
            }

            if (agentName == null || agentName.isEmpty()) {
                //Make sure UI sets agentName using getAgentDetails call.
                throw new BadRequestException(
                    ErrorConstants.ERROR_VALIDATING_AGENT_NAME.getErrorMessage());
            }
        }
        try {
            if (conn.getConnectionType() != null) {
                conn.setModule(restDriver.getModuleTypeUsingSourceType(conn.getConnectionType()));
                connectionId = restDriver.createConnection(conn);
            } else {
                throw new BadRequestException(
                    ErrorConstants.ERROR_CREATING_CONNECTION.getErrorMessage());
            }
        } catch (DgDrvException e) {
            logger.error("Create connection failed: " + e);
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Connection created with id: " + connectionId);

        // Default schedule only for detection. In case of masking it is set to false from UI.
        try {
            if (defaultSchedule != null && defaultSchedule == true) {
                SchedulerDTO schedule = getScheduleInfo(connectionId);
                logger.info("Creating a schedule for connection with id: " + connectionId);
                Boolean scheduleStatus = createDefaultDetectionSchedule(schedule, conn.getModule());
                if (scheduleStatus == true) {
                    logger.info("Default Schedule task for connection id " + connectionId
                        + " completed successfully.");
                } else {
                    logger.error(
                        "Default schedule task for connection id " + connectionId + " failed.");
                }
            } else {
                logger.info("Default Schedule is null");
            }
        } catch (Exception e) {
            logger.error("Error in creating schedule: " + e.getMessage());
            logger
                .info("Deleting the created connection quietly due to error in schedule creation");
            try {
                //deleteConnection(connectionId);
                logger.info("Successfully deleted the connection.");
            } catch (Exception e1) {
                logger.error("Connection deletion failed: " + e1.getMessage());
                throw new InternalServerException("Connection created with id: " + connectionId
                    + " but schedule creation was failed.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            throw new InternalServerException(
                ErrorConstants.ERROR_CREATING_TASK_SCHEDULER.getErrorMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return connectionId;
    }

    /**
     * Delete a connection.
     *
     * @return connectionId
     */
    public Integer deleteConnection(Integer connectionId)
        throws BadRequestException, BadGatewayException {
        if (connectionId != null) {
            try {
                restDriver.deleteConnection(connectionId);
            } catch (DgDrvException e) {
                logger.error("Delete connection failed: " + e);
                throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
            }
        } else {
            throw new BadRequestException(
                "Connection " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage(),
                HttpStatus.BAD_REQUEST);
        }
        return connectionId;
    }

    /**
     * Update a connection.
     *
     * @param connectionDTO the updated connection
     * @return connectionId
     */
    public Integer editConnection(Integer connectionId, ConnectionDTO connectionDTO)
        throws InternalServerException, BadRequestException, BadGatewayException {
        DgConnection updatedConnection = connectionDTO.getConnection();
        Boolean defaultSchedule = connectionDTO.getAutoSchedule();
        if (connectionId == null) {
            throw new BadRequestException(
                "Connection " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage(),
                HttpStatus.BAD_REQUEST);
        }
        try {
            if (updatedConnection != null) {
                if (updatedConnection.getConnectionId() != null) {
                    logger.info("Connection already exists.");
                    /* check if user changed the password or not.*/
                    DgConnection currDbConnection = getConnections(
                        Optional.of(updatedConnection.getConnectionId()))
                        .get(0).getConnection();
                    if (currDbConnection.getPassword() != null
                        && updatedConnection.getPassword() != null) {
                        if (currDbConnection.getPassword()
                            .equals(updatedConnection.getPassword())) {
                            // set password to "" if the password isn't changed.
                            // Don't send encrypted password from saas ui to re encrypt.
                            updatedConnection.setPassword("");
                        }
                    }
                }
                updatedConnection.setModule(connectionDTO.getConnection().getModule());
                connectionId = restDriver.editConnection(connectionId, updatedConnection);
            }
        } catch (DgDrvException e) {
            logger.error("Edit connection failed: " + e);
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Connection edited with id: " + connectionId);
        try {
            if (defaultSchedule != null && defaultSchedule == true) {
                getScheduleInfo(connectionId);
                SchedulerDTO schedule = getScheduleInfo(connectionId);
                logger.info("Creating a schedule for connection with id: " + connectionId);
                Boolean scheduleStatus = createDefaultDetectionSchedule(schedule,
                    connectionDTO.getConnection().getModule());
                if (scheduleStatus == true) {
                    logger.info("Default Schedule task for connection id " + connectionId
                        + " completed successfully.");
                } else {
                    logger.error(
                        "Default schedule task for connection id " + connectionId + " failed.");
                }
            } else {
                logger.info("Default Schedule is null");
            }
        } catch (Exception e) {
            logger.error("Connection edited successfully but failed to schedule a task. " + e);
            throw new InternalServerException(
                ErrorConstants.ERROR_UPDATED_CONNECTION_WITH_SCHEDULER.getErrorMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return connectionId;
    }

    /**
     * Test an existing connection status by connectionId.
     *
     * @param connectionId the existing connection Id
     * @return Boolean
     */
    public Boolean testExistingConnection(Integer connectionId)
        throws BadRequestException, BadGatewayException {
        if (connectionId == null) {
            throw new BadRequestException(
                "Connection " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage(),
                HttpStatus.BAD_REQUEST);
        }
        boolean testConnectionStatus = false;
        try {
            testConnectionStatus = restDriver.testExistingConnection(connectionId);
        } catch (DgDrvException e) {
            logger.error("Test connection failed: " + e);
            throw new BadGatewayException(
                ErrorConstants.ERROR_VALIDATING_CONNECTION_DETAILS.getErrorMessage(),
                HttpStatus.BAD_GATEWAY);
        }
        return testConnectionStatus;
    }

    /**
     * Test a new connection status by connectionDTO while adding a connection.
     *
     * @param connectionDTO the existing connection Id
     * @return Boolean
     */
    public Boolean testNewConnection(ConnectionDTO connectionDTO)
        throws BadRequestException, BadGatewayException {
        DgConnection dgConnection = connectionDTO.getConnection();
        DgConnection updatedConnection = connectionDTO.getConnection();
        if (dgConnection.getModule() == null || dgConnection.getModule().toString().equals("")) {
            throw new BadRequestException(
                "Connection " + ErrorConstants.ERROR_MODULE_NOT_EMPTY.getErrorMessage(),
                HttpStatus.BAD_REQUEST);
        }
        boolean testConnectionStatus = false;
        try {
            if (updatedConnection.getConnectionId() != null) {
                logger.info("Connection already exists.");
                /* check if user changed the password or not.*/
                DgConnection currDbConnection = getConnections(
                    Optional.of(updatedConnection.getConnectionId()))
                    .get(0).getConnection();
                if (currDbConnection.getPassword() != null
                    && updatedConnection.getPassword() != null) {
                    if (currDbConnection.getPassword().equals(updatedConnection.getPassword())) {
                        // set password to "" if the password isn't changed.
                        // Don't send encrypted password from saas ui to re encrypt.
                        updatedConnection.setPassword("");
                    }
                }
            }
            dgConnection.setModule(
                restDriver.getModuleTypeUsingSourceType(dgConnection.getConnectionType()));
            dgConnection.setLocation("Cloud");
            testConnectionStatus = restDriver.testConnection(dgConnection);
        } catch (DgDrvException e) {
            logger.error("Test connection failed: " + e);
            throw new BadGatewayException(
                ErrorConstants.ERROR_VALIDATING_CONNECTION_DETAILS.getErrorMessage(),
                HttpStatus.BAD_GATEWAY);
        }
        return testConnectionStatus;
    }

    /**
     * Fetch metadata of an existing connection by connectionId.
     *
     * @param connectionId the existing connection Id
     * @return List of DatabaseMetadata type.
     */
    public List<DatabaseMetadata> fetchExistingConnectionMetadata(Integer connectionId,
        String moduleType) throws BadRequestException, BadGatewayException {
        if (connectionId == null) {
            throw new BadRequestException("Connection id must be specified as path variable.",
                HttpStatus.BAD_REQUEST);
        }
        List<DatabaseMetadata> response = null;
        try {
            response = restDriver.getConnectionMetadata(connectionId, moduleType);
        } catch (DgDrvException e) {
            logger.error("Error fetching metadata: " + e);
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Metadata fetched successfully");
        return response;
    }

    /**
     * Fetch metadata of new connection by connectionDTO while adding a connection.
     *
     * @param dgConnection the existing connection Id
     * @return List of DatabaseMetadata type.
     */
    public List<DatabaseMetadata> fetchNewConnectionMetadata(DgConnection dgConnection)
        throws BadGatewayException, BadRequestException {
        List<DatabaseMetadata> response = null;
        if (dgConnection == null || dgConnection.getConnectionType() == null) {
            throw new BadRequestException(
                "Connection type must be specified for getting connection Metdata.",
                HttpStatus.BAD_REQUEST);
        }
        try {
            dgConnection.setModule(
                restDriver.getModuleTypeUsingSourceType(dgConnection.getConnectionType()));
            if (dgConnection.getConnectionId() != null) {
                logger.info("Connection already exists.");
                /* check if user changed the password or not.*/
                DgConnection currDbConnection = getConnections(
                    Optional.of(dgConnection.getConnectionId()))
                    .get(0).getConnection();
                if (currDbConnection.getPassword().equals(dgConnection.getPassword())) {
                    // call existing connection metadata if the password isn't changed.
                    // Don't send encrypted password from saas ui to re encrypt.
                    dgConnection.setPassword("");
                }
            }
            dgConnection.setLocation("Cloud");

            response = restDriver.getConnectionMetadata(dgConnection);
        } catch (DgDrvException e) {
            logger.error("Error fetching metadata: " + e);
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Metadata fetched successfully");
        return response;
    }

    private Boolean createDefaultDetectionSchedule(SchedulerDTO schedule, String moduleType)
        throws BadRequestException, BadGatewayException, InternalServerException {
        Boolean status;
        TimeZone tz = TimeZone.getTimeZone("UTC");
        // Quoted "Z" to indicate UTC, no timezone offset
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(tz);
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, delayInMins != null ? delayInMins : 0);
        logger.info("Default scan type is: " + scanType);
        logger.info("Calender Time in Local time zone with delay is: " + c.getTime());
        logger.info("ISO DATE with delay is: " + df.format(c.getTime()));
        String isoTime = df.format((c.getTime()));
        schedule.setScheduleName("ds_" + taskBuilderService.getTimeStampForDgSecure());
        schedule.setScheduleType(taskBuilderService.DETECTION_TYPE);
        schedule.setScanType(scanType);
        schedule.setStartDateTime(isoTime);
        schedule.setSourceModule(moduleType);
        status = schedulerService.schedule(schedule);
        return status;
    }

    private SchedulerDTO getScheduleInfo(Integer connectionId) {
        SchedulerDTO schedulerDTO = new SchedulerDTO();
        List<TaskParamsDTO> taskParams = new ArrayList<>();
        TaskParamsDTO taskParamsDTO = new TaskParamsDTO();
        taskParamsDTO.setSourceId(connectionId);
        taskParams.add(taskParamsDTO);
        schedulerDTO.setTaskParams(taskParams);
        return schedulerDTO;
    }
}
