package com.dataguise.saas.controllers;

import com.dataguise.saas.dto.SchedulerDTO;
import com.dataguise.saas.exception.InternalServerException;
import com.dataguise.saas.exception.BadGatewayException;
import com.dataguise.saas.exception.BadRequestException;
import com.dataguise.saas.service.S3LfaService;
import com.dataguise.saas.service.SchedulerService;
import io.swagger.client.model.DgTaskScheduledExecutionsBean;
import io.swagger.client.model.DgTaskSchedulerTaskHistBean;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@CrossOrigin
@RestController
@ConfigurationProperties(prefix = "dgsecure")
@PropertySource("classpath:application.properties")
@RequestMapping(value = "/api/scheduler")
public class SchedulerController {

    private static Logger logger = Logger.getLogger(SchedulerController.class);

    @Autowired
    SchedulerService schedulerService;

    @Autowired
    private S3LfaService s3LfaService;

    /**
     * Get a list of scheduled tasks
     *
     * @param id
     * @return
     * @throws BadGatewayException
     */
    @RequestMapping(value = {"/", "/{id}"}, method = RequestMethod.GET)
    public List<SchedulerDTO> getSchedule(@PathVariable Optional<Integer> id) throws BadGatewayException, InternalServerException {
        return schedulerService.getSchedule(id);
    }

    /**
     * Delete a schedule.
     *
     * @param id
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.DELETE)
    public Boolean deleteSchedule(@PathVariable Integer id) throws BadRequestException, BadGatewayException {
        return schedulerService.deleteSchedule(id);
    }

    /**
     * Update a schedule.
     *
     * @param id
     * @param schedulerDto
     * @return
     * @throws BadGatewayException
     * @throws InternalServerException
     * @throws BadRequestException
     */
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.PUT)
    public Boolean editSchedule(@PathVariable Integer id, @RequestBody SchedulerDTO schedulerDto) throws BadGatewayException, InternalServerException, BadRequestException {
        return schedulerService.editSchedule(id, schedulerDto);
    }

    /**
     * Schedule task
     *
     * @param schedulerDto
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     * @throws InternalServerException
     */
    @RequestMapping(value = {"/"}, method = RequestMethod.POST)
    public Boolean schedule(@RequestBody SchedulerDTO schedulerDto) throws BadRequestException, BadGatewayException, InternalServerException {
        return schedulerService.schedule(schedulerDto);
    }

    /**
     * Get all task instances by schedule.
     *
     * @param id
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    @RequestMapping(value = {"/getScheduleHistory/{id}"}, method = RequestMethod.GET)
    public List<DgTaskSchedulerTaskHistBean> getScheduleHistory(@PathVariable Integer id)
            throws BadRequestException, BadGatewayException {
        return schedulerService.getScheduleHistory(id);
    }

    /**
     * Get schedule summary by id.
     *
     * @param id
     * @return
     * @throws BadRequestException
     * @throws BadGatewayException
     */
    @RequestMapping(value = {"/getScheduleSummary","/getScheduleSummary/{id}"}, method = RequestMethod.GET)
    public DgTaskScheduledExecutionsBean getScheduleSumamry(@PathVariable Optional<Integer> id)
            throws BadRequestException, BadGatewayException {
        return schedulerService.getScheduleSummary(id);
    }

    /**
     * This API will return the list of S3 buckets in tree format
     * @param sourceId
     * @param dirPath
     * @return
     * @throws BadGatewayException
     */
    @RequestMapping(value = {"/s3bucketstree"})
    public String getS3BucketsTree(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("dirPath") String dirPath) throws BadGatewayException {
        return s3LfaService.getS3BucketsTree(sourceId, dirPath);
    }

    /**
     * This API will return the list of buckets with its directories, subdirectories and files
     * @param sourceId
     * @param dirPath
     * @return
     * @throws BadGatewayException
     */
    @RequestMapping(value = {"/s3objectinfo"})
    public String getS3BucketsDirInfo(
            @RequestParam("sourceId") String sourceId,
            @RequestParam("dirPath") String dirPath) throws BadGatewayException {
        return s3LfaService.getS3BucketsDirInfo(sourceId, dirPath);
    }

}
