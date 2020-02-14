package com.dataguise.saas.service;

import com.dataguise.saas.controllers.SettingsController;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.structures.DetectionTask;
import com.dg.saas.orch.models.structures.ErrorConstants;
import com.dg.saas.orch.models.structures.LFADetectionTask;
import com.dg.saas.orch.models.structures.Modules;
import io.swagger.client.model.CloudTaskBean;
import io.swagger.client.model.DgDiscoverTask;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class DetectionTaskService {

    private static Logger logger = Logger.getLogger(DetectionTaskService.class);

    @Autowired private DgSecureRestDriver restDriver;
    @Autowired private SettingsController settingsController;
    @Autowired private TaskBuilderService taskBuilderService;


    /**
     * Create a detection task
     * @param detectionTask
     * @return
     * @throws BadGatewayException
     */
    public Integer createTask(@RequestBody DgDiscoverTask detectionTask, String sourceModule) throws BadGatewayException {
        Integer taskId = null;
        try {
            taskId = restDriver.createDetectionTask(detectionTask, sourceModule);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Detection task created with id: " + taskId);
        return taskId;
    }

    /**
     * Edit a detection task
     * @param id
     * @param detectionTask
     * @return
     * @throws BadGatewayException
     * @throws BadRequestException
     */
    public Integer editTask(@PathVariable Integer id, @RequestBody DgDiscoverTask detectionTask)
            throws BadGatewayException, BadRequestException {
        Integer taskId = null;
        if( id == null) {
            throw new BadRequestException("Task " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        try {
            taskId = restDriver.editDetectionTask(id, detectionTask);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Detection task edited with id: " + taskId);
        return taskId;
    }

    /**
     * Execute a detection task
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
            taskInstanceId = restDriver.executeDetectionTask(taskId);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("Detection task executed with instance id: " + taskInstanceId);
        return taskInstanceId;
    }

    /**
     * Construct the DgDiscoverTask to create a detection task
     * @param connectionIdList
     * @return
     * @throws BadGatewayException
     * @throws InternalServerException
     */
    public DgDiscoverTask constructDetectionTask(List<Integer> connectionIdList) throws BadGatewayException,InternalServerException {
        DgDiscoverTask discoverTask = null;
        Integer taskInstanceId = null;
        try {
            String taskDetail = taskBuilderService.constructTaskName(taskBuilderService.DETECTION_TYPE);

            DetectionTask.DetectionTaskBuilder builder = new DetectionTask.DetectionTaskBuilder(taskDetail, taskDetail, Modules.RDS)
                    .setTaskType(DetectionTask.TaskTypes.Discover)
                    .setSearchType("Deep")
                    .useExistingSafeList(false)
                    .setSampleDataStart(DetectionTask.SampleDataStart.Top)
                    .setSampleSizeSpecifcation(DetectionTask.SampleSizeSpecification.Rows, 1000)
                    .setAdditionalSampleSize(0.0)
                    .showSampleData(true)
                    .showViews(true);

            builder = taskBuilderService.addTaskBuilderPolicies(builder);

            for (Integer connectionId : connectionIdList) {
                builder.addConnection(connectionId);
            }
            logger.debug("Detection task builder bean to driver: "+builder);
            discoverTask = builder.build().dgDiscoverTask;
        } catch (DgDrvException e) {
            logger.info(e.getMessage());
            throw new BadGatewayException("Driver unable to construct detection task.", HttpStatus.BAD_GATEWAY);
        }
        return discoverTask;
    }

    /**
     * Create LFA-S3 detection task
     * @param lfaDetectionTask
     * @return
     * @throws BadGatewayException
     */
    public Integer createLFATask(@RequestBody CloudTaskBean lfaDetectionTask) throws BadGatewayException {
        Integer taskId = null;
        try {
            taskId = restDriver.createLFADetectionTask(lfaDetectionTask);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        logger.info("LFA Detection task created with id: " + taskId);
        return taskId;
    }

    /**
     * Construct the CloudTaskBean to create LFA-S3 detection task.
     * @param scanLocations
     * @return
     * @throws BadGatewayException
     * @throws InternalServerException
     */
    public CloudTaskBean constructLFADetectionTask(List<String> scanLocations) throws BadGatewayException,InternalServerException {
        CloudTaskBean lfaDiscoverTask = null;
        List<String> excludeList = new ArrayList<>();
        try {
            String taskDetail = taskBuilderService.constructTaskName(taskBuilderService.DETECTION_TYPE);
            LFADetectionTask.LFADetectionTaskBuilder builder = new LFADetectionTask.LFADetectionTaskBuilder(taskDetail, taskDetail)
                    .setTaskType(LFADetectionTask.TaskTypes.Discover)
                    .setBatchSize(30)  //Default batch size
                    .setClusterProvisionType(LFADetectionTask.ClusterProvisionType.Manual)
                    .setDeleteOriginal(false)
                    .setFullFileScan(false)
                    .setDiscoveryCriteria(false)
                    .setIncrementalDiscovery(true)
                    .setSkipped(true)
                    .setStructured(false)
                    .setVerifyStructure(false)
                    .setScanLocations(scanLocations)
                    .setClusterType(LFADetectionTask.ClusterTypes.UnixFiles)
                    .setExcludedFileExtensions(excludeList)
                    .setExcludedScanPathList(excludeList)
                    .setSamplingId(15)  //Sampling Id=15 for moduleType='file'(Top 1000 rows)
                    .setOutputColumnForm("replace")
                    .setValueSeparator(",");

            builder = taskBuilderService.addTaskBuilderPolicies(builder);

            logger.debug("LFA Detection task builder bean to driver: "+builder);
            lfaDiscoverTask = builder.build().dgLFADiscoverTask;
        } catch (DgDrvException e) {
            logger.info(e.getMessage());
            throw new BadGatewayException(ErrorConstants.ERROR_CONSTRUCTING_DETECTION_TASK.getErrorMessage(), HttpStatus.BAD_GATEWAY);
        }
        return lfaDiscoverTask;
    }

}
