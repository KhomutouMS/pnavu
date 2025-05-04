package com.example.fakemaleru.controller;

import com.example.fakemaleru.exceptions.WrongRequest;
import com.example.fakemaleru.logging.NoLog;
import com.example.fakemaleru.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logs")
public class LogController {
    private final LogService logService;


    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @Operation(summary = "Retrieve logs for a specific date",
            description = "Fetch logs from the system for the specified date.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved logs"),
        @ApiResponse(responseCode = "400", description = "Invalid date format"),
        @ApiResponse(responseCode = "500", description = "Error retrieving logs")
    })
    @GetMapping
    @NoLog
    public ResponseEntity<String> getLogsForDate(
            @Parameter(description = "Date for which logs are to be retrieved", required = true)
            @RequestParam("date") String dateStr) {

        if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return ResponseEntity.badRequest().body("Invalid date format: " + dateStr);
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date: " + e.getMessage());
        }

        try {
            String logs = logService.getLogsForDate(date);
            return ResponseEntity.ok(logs);
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Error retrieving logs: " + e.getMessage());
        }
    }


    @PostMapping("/create")
    @Operation(summary = "Create log processing task",
            description =
                    "Creates a new log processing task with the "
                            + "provided content and returns a task ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Log task successfully created",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(type = "string", description = "Task ID"))),
        @ApiResponse(responseCode = "400", description = "Invalid request body"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> createLog(
            @Parameter(description = "Log content to process", required = true)
            @RequestBody String content,
            @RequestParam("date") String dateStr) { // Изменено на String

        if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return ResponseEntity.badRequest().body("Invalid date format: " + dateStr);
        }

        LocalDate date;
        try {
            date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date: " + e.getMessage());
        }

        String taskId = logService.createLogTask(content, date);
        return ResponseEntity.accepted().body(taskId);
    }

    @GetMapping("/{taskId}/status")
    @Operation(summary = "Get log task status",
            description = "Returns the current status of a log processing task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved task status",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LogService.LogTask.class))),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LogService.LogTask> getLogStatus(
            @Parameter(description = "Log task ID", required = true)
            @PathVariable String taskId) {
        LogService.LogTask task = logService.getTaskStatus(taskId);
        if (task == null) {
            throw new WrongRequest("wrong Id");
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/{taskId}/download")
    @NoLog
    @Operation(summary = "Download log file",
            description = "Downloads the log file for a completed processing task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully downloaded log file",
                    content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "102", description = "Processing - task not yet completed"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<ByteArrayResource> downloadLog(
            @Parameter(description = "Log task ID", required = true)
            @PathVariable String taskId) {
        try {
            LogService.LogTask task = logService.getTaskStatus(taskId);
            if (task == null) {
                return ResponseEntity.notFound().build();
            }

            if (task.getStatus() != LogService.LogTask.Status.COMPLETED) {
                return ResponseEntity.notFound().build();
            }

            byte[] data = logService.getLogFile(taskId);
            ByteArrayResource resource = new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=log_" + taskId + ".txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(data.length)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}