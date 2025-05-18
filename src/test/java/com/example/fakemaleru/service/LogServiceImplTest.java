package com.example.fakemaleru.service;

import com.example.fakemaleru.service.impl.LogServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class LogServiceImplTest {

    private LogServiceImpl logService;
    private String logFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        logService = new LogServiceImpl();
        logFilePath = "logs/test_log.txt";
        logService.logFilePath = logFilePath;

        // Create log directory and test log file
        new File("logs").mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath))) {
            writer.write("2025-05-10 Sample log entry 1\n");
            writer.write("2025-05-10 Sample log entry 2\n");
            writer.write("2025-05-11 Sample log entry 3\n");
        }
    }

    @Test
    public void testGetLogsForDate() throws IOException {
        LocalDate date = LocalDate.of(2025, 5, 10);
        String logs = logService.getLogsForDate(date);
        assertTrue(logs.contains("Sample log entry 1"));
        assertTrue(logs.contains("Sample log entry 2"));
        assertFalse(logs.contains("Sample log entry 3"));
    }

    @Test
    public void testGetLogsForUnknownDate() throws IOException {
        LocalDate date = LocalDate.of(2025, 5, 12);
        String logs = logService.getLogsForDate(date);
        assertEquals("No logs found for date: 2025-05-12", logs);
    }

    @Test
    public void testCreateLogTask() {
        LocalDate date = LocalDate.now();
        String taskId = logService.createLogTask("Sample log content", date);
        assertNotNull(taskId);
        assertTrue(logService.getTaskStatus(taskId) != null);
    }

    @Test
    public void testGetTaskStatus() {
        LocalDate date = LocalDate.now();
        String taskId = logService.createLogTask("Sample log content", date);
        LogService.LogTask task = logService.getTaskStatus(taskId);
        assertEquals(LogService.LogTask.Status.PROCESSING, task.getStatus());
    }

    @Test
    public void testGetLogFile() throws IOException, InterruptedException {
        LocalDate date = LocalDate.now();
        String taskId = logService.createLogTask("Sample log content", date);

        // Wait for the task to complete
        Thread.sleep(35000); // Adjust the time based on processing delay

        byte[] logFile = logService.getLogFile(taskId);
        assertNotNull(logFile);
        assertTrue(logFile.length > 0);
    }

    @Test
    public void testGetLogFileForIncompleteTask() throws IOException {
        LocalDate date = LocalDate.now();
        String taskId = logService.createLogTask("Sample log content", date);

        // Simulate immediate retrieval before completion
        byte[] logFile = logService.getLogFile(taskId);
        assertNull(logFile, "Log file should be null for incomplete task");
    }

    @Test
    public void testHandleIOExceptionInGetLogsForDate() throws IOException {
        logService.logFilePath = "invalid/path/to/logfile.txt";
        LocalDate date = LocalDate.now();
        Exception exception = assertThrows(IOException.class, () -> {
            logService.getLogsForDate(date);
        });
        assertTrue(exception.getMessage().contains("Log file does not exist"));
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(logFilePath));
        Files.deleteIfExists(Paths.get("logs"));
    }
}