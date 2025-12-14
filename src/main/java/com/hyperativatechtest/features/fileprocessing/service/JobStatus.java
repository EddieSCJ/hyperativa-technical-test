package com.hyperativatechtest.features.fileprocessing.service;

public interface JobStatus {
    String getId();
    String getStatus();
    String getFileName();
    Integer getTotalRecords();
    Integer getProcessedRecords();
    Integer getFailedRecords();
    String getErrorMessage();
}

