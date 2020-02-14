package com.dataguise.saas.dto;

import com.google.gson.Gson;

public class SummaryDetailsDTO {

    String name;

    String serverType;
    Integer cleaned;
    Integer discovered;
    Integer protectedData;
    Integer exposed;
    Integer unscanned;

    Integer totalCount;
    Integer cloudCount;
    Integer onPremisesCount;

    Integer unprocessed;
    String cleanSize;
    String  protectedSize;
    String exposedSize;
    String unscannedSize;
    String unprocessedSize;
    String totalSize;

    String cleanPercent;
    String protectedPercent;
    String exposedPercent;
    String unscannedPercent;
    String unprocessedPercent;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public Integer getCleaned() {
        return cleaned;
    }

    public void setCleaned(Integer cleaned) {
        this.cleaned = cleaned;
    }

    public Integer getDiscovered() {
        return discovered;
    }

    public void setDiscovered(Integer discovered) {
        this.discovered = discovered;
    }

    public Integer getProtectedData() {
        return protectedData;
    }

    public void setProtectedData(Integer protectedData) {
        this.protectedData = protectedData;
    }

    public Integer getExposed() {
        return exposed;
    }

    public void setExposed(Integer exposed) {
        this.exposed = exposed;
    }

    public Integer getUnscanned() {
        return unscanned;
    }

    public void setUnscanned(Integer unscanned) {
        this.unscanned = unscanned;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getCloudCount() {
        return cloudCount;
    }

    public void setCloudCount(Integer cloudCount) {
        this.cloudCount = cloudCount;
    }

    public Integer getOnPremisesCount() {
        return onPremisesCount;
    }

    public void setOnPremisesCount(Integer onPremisesCount) {
        this.onPremisesCount = onPremisesCount;
    }

    public Integer getUnprocessed() {
        return unprocessed;
    }

    public void setUnprocessed(Integer unprocessed) {
        this.unprocessed = unprocessed;
    }

    public String getCleanSize() {
        return cleanSize;
    }

    public void setCleanSize(String cleanSize) {
        this.cleanSize = cleanSize;
    }

    public String getProtectedSize() {
        return protectedSize;
    }

    public void setProtectedSize(String protectedSize) {
        this.protectedSize = protectedSize;
    }

    public String getExposedSize() {
        return exposedSize;
    }

    public void setExposedSize(String exposedSize) {
        this.exposedSize = exposedSize;
    }

    public String getUnscannedSize() {
        return unscannedSize;
    }

    public void setUnscannedSize(String unscannedSize) {
        this.unscannedSize = unscannedSize;
    }

    public String getUnprocessedSize() {
        return unprocessedSize;
    }

    public void setUnprocessedSize(String unprocessedSize) {
        this.unprocessedSize = unprocessedSize;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public String getCleanPercent() {
        return cleanPercent;
    }

    public void setCleanPercent(String cleanPercent) {
        this.cleanPercent = cleanPercent;
    }

    public String getProtectedPercent() {
        return protectedPercent;
    }

    public void setProtectedPercent(String protectedPercent) {
        this.protectedPercent = protectedPercent;
    }

    public String getExposedPercent() {
        return exposedPercent;
    }

    public void setExposedPercent(String exposedPercent) {
        this.exposedPercent = exposedPercent;
    }

    public String getUnscannedPercent() {
        return unscannedPercent;
    }

    public void setUnscannedPercent(String unscannedPercent) {
        this.unscannedPercent = unscannedPercent;
    }

    public String getUnprocessedPercent() {
        return unprocessedPercent;
    }

    public void setUnprocessedPercent(String unprocessedPercent) {
        this.unprocessedPercent = unprocessedPercent;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
