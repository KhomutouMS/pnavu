package com.example.fakemaleru.service;

import com.example.fakemaleru.service.impl.LogServiceImpl;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

public interface LogService {
    String getLogsForDate(LocalDate date) throws IOException;

    String createLogTask(String content, LocalDate date);

    LogServiceImpl.LogTask getTaskStatus(String taskId);

    byte[] getLogFile(String taskId) throws IOException;

    @Getter
    @Setter
    class LogTask {
        public enum Status {
            PENDING,
            PROCESSING,
            COMPLETED,
            FAILED
        }

        private final String id;
        private final String content;
        private LogServiceImpl.LogTask.Status status;
        private String filePath;
        private String errorMessage;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        private LocalDate date;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
        private final LocalDateTime createdAt;

        public LogTask(String id, String content, LocalDate date) {
            this.id = id;
            this.content = content;
            this.status = LogServiceImpl.LogTask.Status.PENDING;
            this.date = date;
            this.createdAt = LocalDateTime.now();
        }
    }
}