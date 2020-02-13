package com.dataguise.saas.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.client.model.DgTaskScheduleInfoBean;
import io.swagger.client.model.DgTaskScheduledExecutionsBean;

import java.util.List;

public class SchedulerDTO {

    @JsonIgnoreProperties({"connectionName", "taskId", "taskName", "taskType", "lastTaskInstanceTimeEnd",
        "lastTaskInstanceTimeStart", "lastTaskInstanceStatus", "taskIds"})
    private DgTaskScheduleInfoBean schedule = null;
    private DgTaskScheduledExecutionsBean scheduleSummary = null;
    private String sourceModule;
    private List<TaskParamsDTO> taskParams = null;
    private List<String> scanLocations;

    private String scheduleName = null;

    public void setSourceModule(String sourceModule) {
        this.sourceModule = sourceModule;
    }

    private String scheduleType = null;

    private List<String> scheduleDay = null;

    //ISO DATE FORMAT
    private String startDateTime = null;

    //Weekly/Daily using hourly interval 24:00
    private String scanType = null;

    private String scheduleInterval;

    //For now we fix this. No need this from UI.
    private String scheduleScanEndDate = null;

    private Boolean scheduledEventEnabled = null;

    private Integer id = null;

    private String moduleType = null;

    @JsonIgnore
    public List<TaskParamsDTO> getTaskParams() {
        return taskParams;
    }

    @JsonProperty
    public void setTaskParams(List<TaskParamsDTO> taskParams) {
        this.taskParams = taskParams;
    }

    @JsonIgnore
    public String getScheduleName() {
        return scheduleName;
    }

    @JsonProperty
    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    @JsonIgnore
    public String getScheduleType() {
        return scheduleType;
    }

    @JsonProperty
    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    @JsonIgnore
    public String getScheduleInterval() {
        return scheduleInterval;
    }

    @JsonProperty
    public void setScheduleInterval(String scheduleInterval) {
        this.scheduleInterval = scheduleInterval;
    }

    @JsonIgnore
    public Integer getId() {
        return id;
    }

    @JsonProperty
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public String getModuleType() {
        return moduleType;
    }

    @JsonProperty
    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    @JsonIgnore
    public List<String> getScheduleDay() {
        return scheduleDay;
    }

    @JsonProperty
    public void setScheduleDay(List<String> scheduleDay) {
        this.scheduleDay = scheduleDay;
    }

    @JsonIgnore
    public String getScheduleScanEndDate() {
        return scheduleScanEndDate;
    }

    @JsonProperty
    public void setScheduleScanEndDate(String scheduleScanEndDate) {
        this.scheduleScanEndDate = scheduleScanEndDate;
    }

    @JsonIgnore
    public String getStartDateTime() {
        return startDateTime;
    }

    @JsonProperty
    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    @JsonIgnore
    public String getScanType() {
        return scanType;
    }

    @JsonProperty
    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public DgTaskScheduleInfoBean getSchedule() {
        return schedule;
    }

    public void setSchedule(DgTaskScheduleInfoBean schedule) {
        this.schedule = schedule;
    }

    public DgTaskScheduledExecutionsBean getScheduleSummary() {
        return scheduleSummary;
    }

    public void setScheduleSummary(DgTaskScheduledExecutionsBean scheduleSummary) {
        this.scheduleSummary = scheduleSummary;
    }
    public String getSourceModule() {
        return sourceModule;
    }

    public List<String> getScanLocations() { return scanLocations; }

    public void setScanLocations(List<String> scanLocations) { this.scanLocations = scanLocations; }
}
