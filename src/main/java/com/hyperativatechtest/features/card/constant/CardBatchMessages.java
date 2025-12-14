package com.hyperativatechtest.features.card.constant;

public final class CardBatchMessages {

    private CardBatchMessages() {
    }

    public static final String FILE_UPLOADED_SUCCESSFULLY = "File uploaded successfully. Processing started.";
    public static final String FILE_IS_EMPTY = "File is empty";
    public static final String ONLY_TXT_FILES_ALLOWED = "Only TXT files are allowed";
    public static final String ERROR_UPLOADING_FILE = "Error uploading file";
    public static final String JOB_NOT_FOUND = "Job not found";

    public static class Status {
        public static final String PENDING = "PENDING";
        public static final String PROCESSING = "PROCESSING";
        public static final String COMPLETED = "COMPLETED";
        public static final String FAILED = "FAILED";
    }
}

