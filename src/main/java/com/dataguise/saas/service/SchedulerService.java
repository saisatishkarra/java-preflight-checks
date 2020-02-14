package com.dataguise.saas.service;

import com.dataguise.saas.dto.SchedulerDTO;
import com.dataguise.saas.dto.TaskParamsDTO;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.exception.InternalServerException;
import com.dg.saas.orch.client.DgSecureRestDriver;
import com.dg.saas.orch.exception.DgDrvException;
import com.dg.saas.orch.models.structures.ErrorConstants;
import com.dg.saas.orch.models.structures.Modules;
import io.swagger.client.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * The Scheduler service
 */
@Service
public class SchedulerService {

    private static Logger logger = Logger.getLogger(SchedulerService.class);

    private static final String SCHEDULE_END_DATE = "12/01/2035 17:00:00";

    @Autowired
    private DgSecureRestDriver restDriver;

    @Autowired
    private DetectionTaskService detectionTaskService;

    @Autowired
    private ProtectionTaskService protectionTaskService;

    @Autowired
    private TaskBuilderService taskBuilderService;

    /**
     * Create schedule
     *
     * @param schedulerDto
     */
    public Boolean schedule(SchedulerDTO schedulerDto)
            throws BadRequestException, BadGatewayException, InternalServerException {
        Boolean status = false;
        String taskType = null;
        List<Integer> taskIdList = new ArrayList<>();
        List<TaskParamsDTO> taskParams = null;
        if (schedulerDto.getScheduleName() == null || schedulerDto.getScheduleName().isEmpty()) {
            logger.error("Schedule name is missing.");
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_SCHEDULER_NAME.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }

        if (schedulerDto.getScheduleType() == null) {
            logger.error("Schedule Type is a required parameter and possible values are Detection/Masking");
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_SCHEDULER_TYPE.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }

        if (schedulerDto.getTaskParams() == null) {
            logger.error("Task parameters for either detection or masking is missing");
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_TASK_TYPE.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }

        if (schedulerDto.getScanType() == null) {
            logger.error("Scan Type is a required parameter and possible values are Weekly/Daily");
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_SCAN_TYPE.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }

        if (schedulerDto.getStartDateTime() == null) {
            logger.error("Start date and time cannot be empty.");
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_START_DATE.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }

        schedulerDto = formatScheduleDTO(schedulerDto);
        taskParams = schedulerDto.getTaskParams();
        if (schedulerDto.getSourceModule() != null &&
                !schedulerDto.getSourceModule().equalsIgnoreCase(Modules.S3LFA.name())) {

            if (schedulerDto.getScheduleType().equalsIgnoreCase(taskBuilderService.DETECTION_TYPE)) {
                /* Single detection task for multiple sources. */
                Integer detectionTaskInstanceId = null;
                taskType = taskBuilderService.DETECTION_TYPE;
                List<Integer> connectionIdList = new ArrayList<>();
                for (TaskParamsDTO taskParamsDTO : taskParams) {
                    connectionIdList.add(taskParamsDTO.getSourceId());
                }
                DgDiscoverTask discoverTask = detectionTaskService.constructDetectionTask(connectionIdList);
                detectionTaskInstanceId = detectionTaskService.createTask(discoverTask, schedulerDto.getSourceModule());
                if (detectionTaskInstanceId != null) {
                    taskIdList.add(detectionTaskInstanceId);
                }

            } else if (schedulerDto.getScheduleType().equalsIgnoreCase(taskBuilderService.PROTECTION_TYPE)) {
            /* One taskParamsDTO consists of one complete connection details.
            In Masking create one protection task for each TaskParamsDTO. */
                taskType = taskBuilderService.PROTECTION_TYPE;
                for (TaskParamsDTO taskParamsDTO : taskParams) {
                    DgMaskerTaskDTO protectionTask = protectionTaskService.constructProtectionTask(taskParamsDTO, schedulerDto.getSourceModule());
                    System.out.println(protectionTask);
                    Integer protectionTaskInstanceId = protectionTaskService.createTask(protectionTask, schedulerDto.getSourceModule());
                    if (protectionTaskInstanceId != null) {
                        taskIdList.add(protectionTaskInstanceId);
                    }
                }
            }
        } else {
            if (schedulerDto.getScheduleType().equalsIgnoreCase(taskBuilderService.DETECTION_TYPE)) {
                taskType = taskBuilderService.DETECTION_TYPE;
                List<Integer> connectionIdList = new ArrayList<>();
                for (TaskParamsDTO taskParamsDTO : taskParams) {
                    connectionIdList.add(taskParamsDTO.getSourceId());
                }
                CloudTaskBean cloudDetectionTask = detectionTaskService.constructLFADetectionTask(
                        schedulerDto.getScanLocations());
                cloudDetectionTask.setClusterName(schedulerDto.getTaskParams().get(0).getSourceName());
                Integer lfaDetectionTaskInstanceId = detectionTaskService.createLFATask(cloudDetectionTask);
                if (lfaDetectionTaskInstanceId != null) {
                    taskIdList.add(lfaDetectionTaskInstanceId);
                }
            }
        }

        DgTaskScheduleInfoBean taskScheduleBeanInfoBean = new DgTaskScheduleInfoBean();
        taskScheduleBeanInfoBean.setId(0);
        taskScheduleBeanInfoBean.setScheduleName(schedulerDto.getScheduleName());
        taskScheduleBeanInfoBean.setScheduleInterval(schedulerDto.getScheduleInterval());
        taskScheduleBeanInfoBean.setModuleType(Modules.valueOf(schedulerDto.getSourceModule()).getModuleType());
        taskScheduleBeanInfoBean.setTaskType(taskType);
        taskScheduleBeanInfoBean.scheduleScanStartDate(schedulerDto.getStartDateTime());
        taskScheduleBeanInfoBean.setScheduleScanEndDate(SCHEDULE_END_DATE);
        taskScheduleBeanInfoBean.setScheduleType(schedulerDto.getScanType());
        taskScheduleBeanInfoBean.setTaskIds(taskIdList);
        taskScheduleBeanInfoBean.setScheduleDay(schedulerDto.getScheduleDay());
        taskScheduleBeanInfoBean.setTimeZone("UTC");

        logger.info("Task info bean to controller: " + taskScheduleBeanInfoBean);
        try {
            if (taskType.equals(taskBuilderService.DETECTION_TYPE)) {
                status = isScheduleTaskInfoSaved(restDriver.scheduleTask(taskScheduleBeanInfoBean));
            } else if (taskType.equals(taskBuilderService.PROTECTION_TYPE)) {
                status = isScheduleTaskInfoSaved(restDriver.scheduleTask(taskScheduleBeanInfoBean));
            } else {
                throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_TASK_TYPE.getErrorMessage(), HttpStatus.BAD_REQUEST);
            }
        } catch (DgDrvException e) {
            logger.error("Error creating the schedule.");
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return status;

    }

    /**
     * Get all scheduled events.
     *
     * @return List<DgTaskScheduleInfoBean>
     */
    public List<SchedulerDTO> getSchedule(Optional<Integer> scheduleId) throws BadGatewayException,
            InternalServerException {
        List<SchedulerDTO> scheduleDetails = new ArrayList<>();
        List<DgTaskScheduleInfoBean> scheduleInfoBeanList = null;
        try {
            if (scheduleId.isPresent() && scheduleId.get() > 0) {
                scheduleInfoBeanList = restDriver.getScheduledTasks(scheduleId.get());
            } else {
                scheduleInfoBeanList = restDriver.getScheduledTasks(0);
            }
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        if (!scheduleInfoBeanList.isEmpty()) {

            scheduleInfoBeanList.sort((obj1, obj2) -> obj2.getId().compareTo(obj1.getId()));
            scheduleInfoBeanList.stream().forEach(obj -> {
                        if (obj.getScheduleStatus().toLowerCase().contains("Error".toLowerCase()) ||
                                obj.getScheduleStatus().length() > 30) {
                            obj.setScheduleStatus("Failed");
                        }

                    }
            );


            for (DgTaskScheduleInfoBean scheduleInfoBean : scheduleInfoBeanList) {
                DgTaskScheduledExecutionsBean scheduleSummary = null;
                SchedulerDTO schedule = new SchedulerDTO();
                try {
                    scheduleSummary = getScheduleSummary(Optional.of(scheduleInfoBean.getId()));
                } catch (Exception e) {
                    logger.error("Error retrieving the schedule summary");
                    //TODO: Fix bug on edit schedule to update schedule record and return same scheduleId.
                    //throw new InternalServerException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                if (scheduleInfoBean.getModuleType() != null &&
                        scheduleInfoBean.getModuleType().equalsIgnoreCase(Modules.S3LFA.getModuleType())
                        && scheduleInfoBean.getTaskType().equalsIgnoreCase(Modules.S3LFA.getModuleType())) {
                    DgScheduledLinkedTaskDetails linkedTaskDetails = getLFAScheduledLinkedTaskDetails(scheduleInfoBean);
                    scheduleInfoBean.setLinkedTaskDetails(linkedTaskDetails);
                }
                schedule.setSchedule(scheduleInfoBean);
                schedule.setScheduleSummary(scheduleSummary);
                scheduleDetails.add(schedule);
            }
        }

        return scheduleDetails;
    }

    /**
     * Method to set LinkedTaskDetails of LFA-S3 task modules.
     *
     * @param scheduleInfoBean
     * @return
     */

    public DgScheduledLinkedTaskDetails getLFAScheduledLinkedTaskDetails(DgTaskScheduleInfoBean scheduleInfoBean) {
        DgScheduledLinkedTaskDetails linkedTaskDetails = new DgScheduledLinkedTaskDetails();
        linkedTaskDetails.setTaskType(scheduleInfoBean.getLinkedTaskDetails().getTaskType().equalsIgnoreCase("DA")
                ? taskBuilderService.DETECTION_TYPE : taskBuilderService.PROTECTION_TYPE);
        linkedTaskDetails.setConnectionNames(scheduleInfoBean.getLinkedTaskDetails().getConnectionNames());
        linkedTaskDetails.setTaskName(scheduleInfoBean.getTaskName());
        linkedTaskDetails.setTaskId(scheduleInfoBean.getTaskId());
        return linkedTaskDetails;
    }

    /**
     * Delete a scheduled task
     */
    public Boolean deleteSchedule(Integer scheduleId) throws BadRequestException, BadGatewayException {
        if (scheduleId == null) {
            throw new BadRequestException("Schedule " + ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        try {
            return restDriver.deleteScheduleTask(scheduleId);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Update/Edit a schedule
     *
     * @param schedulerDto
     * @param scheduleId
     */
    public Boolean editSchedule(@PathVariable Integer scheduleId, @RequestBody SchedulerDTO schedulerDto)
            throws BadGatewayException, InternalServerException, BadRequestException {
        Boolean status = false;

        if (schedulerDto.getScheduleName() == null || schedulerDto.getScheduleName().isEmpty()) {
            logger.error("Schedule name is missing.");
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_SCHEDULER_NAME.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }

        if (schedulerDto.getScanType() == null) {
            logger.error("Scan Type is a required parameter and possible values are Weekly/Daily");
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_SCAN_TYPE.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }
        if (schedulerDto.getStartDateTime() == null) {
            logger.error("Start date and time cannot be empty.");
            throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_START_DATE.getErrorMessage(), HttpStatus.BAD_REQUEST);
        }

        if (scheduleId != null) {
            List<SchedulerDTO> existingScheduleList = getSchedule(Optional.of(scheduleId));
            DgTaskScheduleInfoBean existingSchedule;
            if (!existingScheduleList.isEmpty()) {
                existingSchedule = existingScheduleList.get(0).getSchedule();
            } else {
                throw new BadGatewayException(ErrorConstants.ERROR_GETTING_SCHEDULER_BY_ID.getErrorMessage(), HttpStatus.BAD_GATEWAY);
            }
            logger.info("Existing task info bean for schedule:" + existingSchedule);
            formatScheduleDTO(schedulerDto);
            logger.info("Formatted schedule DTO from UI: " + schedulerDto);

            List<Integer> taskIdList = new ArrayList<>();
            taskIdList.add(existingSchedule.getTaskId());

            //existingSchedule.setId(0);
            existingSchedule.setScheduleName(schedulerDto.getScheduleName());
            existingSchedule.setScheduleInterval(schedulerDto.getScheduleInterval());
            if (schedulerDto.getSourceModule() != null &&
                    schedulerDto.getSourceModule().equalsIgnoreCase(Modules.S3LFA.getModuleType()))
                existingSchedule.setTaskType(taskBuilderService.DETECTION_TYPE);  // In getScheduler call taskType comes as 'file' from controller backend.

            //existingSchedule.setTaskType(schedulerDto.getTaskType());
            existingSchedule.scheduleScanStartDate(schedulerDto.getStartDateTime());
            existingSchedule.setScheduleScanEndDate(SCHEDULE_END_DATE);
            existingSchedule.setScheduleType(schedulerDto.getScanType());
            existingSchedule.setTaskId(null);
            existingSchedule.setExecutionType(null);
            existingSchedule.setTaskIds(taskIdList);
            existingSchedule.setScheduleDay(schedulerDto.getScheduleDay());
            existingSchedule.setTimeZone("UTC");
            //By default it is set to true no matter what we pass.
            //existingSchedule.setEnabled(true);

            logger.info("Updated existing task info bean to controller: " + existingSchedule);
            try {
                if (existingSchedule.getTaskType().equalsIgnoreCase(taskBuilderService.DETECTION_TYPE)) {
                    status = isScheduleTaskInfoSaved(restDriver.editScheduleTask(existingSchedule));
                } else if (existingSchedule.getTaskType().equalsIgnoreCase(taskBuilderService.PROTECTION_TYPE)) {
                    status = isScheduleTaskInfoSaved(restDriver.editScheduleTask(existingSchedule));
                } else {
                    throw new BadRequestException(ErrorConstants.ERROR_VALIDATING_TASK_TYPE.getErrorMessage(), HttpStatus.BAD_REQUEST);
                }
            } catch (DgDrvException e) {
                logger.error("Error updating the schedule.");
                throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
            }

        }
        return status;
    }

    /**
     * Get schedule event history by scheduleId
     *
     * @param scheduleId
     */
    public List<DgTaskSchedulerTaskHistBean> getScheduleHistory(Integer scheduleId)
            throws BadRequestException, BadGatewayException {
        List<DgTaskSchedulerTaskHistBean> taskSchedulerTaskHistBeanList = null;
        if (scheduleId == null) {
            throw new BadRequestException("Schedule "+ ErrorConstants.ERROR_MISSING_PATH_VARIABLE.getErrorMessage());
        }
        try {
            taskSchedulerTaskHistBeanList = restDriver.getTaskInstancesBySchedule(scheduleId);
        } catch (DgDrvException e) {
            throw new BadGatewayException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
        return taskSchedulerTaskHistBeanList;
    }

    /**
     * Get schedule summary by scheduleId
     *
     * @param scheduleId
     */
    public DgTaskScheduledExecutionsBean getScheduleSummary(Optional<Integer> scheduleId)
            throws BadRequestException, BadGatewayException {
        DgTaskScheduledExecutionsBean scheduleSummary = new DgTaskScheduledExecutionsBean();
        try {
            if (scheduleId.isPresent() && scheduleId.get() > 0) {
                scheduleSummary = restDriver.getScheduleSummary(scheduleId.get());
            } else {
                scheduleSummary = restDriver.getScheduleSummary(0);
            }
        } catch (DgDrvException e) {
            logger.error("Error retrieving the schedule summary"); // Not throwing an error in this case because GUI require default values
            Long defaultSummary = new Long(0);
            scheduleSummary.setFailedExecutions(defaultSummary);
            scheduleSummary.setInProgressExecutions(defaultSummary);
            scheduleSummary.setSuccessfulExecutions(defaultSummary);
            scheduleSummary.setTotalDetectionScheduler(defaultSummary);
            scheduleSummary.setTotalExecutions(defaultSummary);
            scheduleSummary.setTotalMaskingScheduler(defaultSummary);
        }
        return scheduleSummary;
    }


    public SchedulerDTO formatScheduleDTO(SchedulerDTO schedulerDto) throws BadRequestException, InternalServerException, BadGatewayException {
        String startDateTime = null;
        String weekday = null;
        List<String> weekdays = new ArrayList<>();
        java.util.Date localZoneDate = null;

        //TODO: Accept date in ISO 8601 format and convert it into controller readable format.
        String isoInputDate = schedulerDto.getStartDateTime();
        logger.info("Input ISO date from UI: " + isoInputDate);

        try {
            localZoneDate = Date.from(ZonedDateTime.parse(isoInputDate).toInstant());
            logger.info("Local time zone of UI:" + localZoneDate.toString()); //ISO TO LOCAL TIME ZONE
        } catch (DateTimeParseException e) {
            throw new InternalServerException(ErrorConstants.ERROR_PARSE_ISO_DATE.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SimpleDateFormat dof = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        SimpleDateFormat dof1 = new SimpleDateFormat("EEEE");

        TimeZone tz = TimeZone.getTimeZone("UTC");
        dof.setTimeZone(tz);
        dof1.setTimeZone(tz);

        startDateTime = dof.format(localZoneDate);
        logger.info("FINAL CORRECT DATE: " + startDateTime);

        weekday = dof1.format(localZoneDate);
        logger.info("FINAL CORRECT DAY: " + weekday);

        if (schedulerDto.getScanType().equals("Daily")) {
            schedulerDto.setScheduleInterval("24:00");
            schedulerDto.setScheduleDay(weekdays);
            schedulerDto.setScanType("Hourly");
        } else if (schedulerDto.getScanType().equals("Weekly")) {
            weekdays.add(weekday);
            schedulerDto.setScheduleInterval("");
            schedulerDto.setScheduleDay(weekdays);
            schedulerDto.setScanType("Weekly");
        }
        schedulerDto.setStartDateTime(startDateTime);
        return schedulerDto;
    }

    public Boolean isScheduleTaskInfoSaved(List<DgSchedulerSavedTaskInfo> dgSchedulerSavedTaskInfoList) {
        if (dgSchedulerSavedTaskInfoList != null && !dgSchedulerSavedTaskInfoList.isEmpty()) {
            return dgSchedulerSavedTaskInfoList.get(0).getResult();
        }
        return false;
    }


}










