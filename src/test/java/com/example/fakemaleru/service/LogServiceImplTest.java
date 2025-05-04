package com.example.fakemaleru.service;

import com.example.fakemaleru.exceptions.WrongRequest;
import com.example.fakemaleru.service.impl.LogServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class LogServiceImplTest {

    @InjectMocks
    private LogServiceImpl logService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logService = new LogServiceImpl();
        logService.setLogFilePath("test-logs.txt"); // Устанавливаем путь к файлу
    }

    @Test
    void testGetLogsForDate_Success() throws IOException {
        // Создаем тестовый файл с логами
        Path testLogFile = Path.of("test-logs.txt");
        Files.write(testLogFile, Arrays.asList(
                "2023-04-28 Log entry 1",
                "2023-04-28 Log entry 2",
                "2023-04-29 Log entry 3"
        ));

        String result = logService.getLogsForDate(LocalDate.of(2023, 4, 28));
        assertEquals("2023-04-28 Log entry 1\n2023-04-28 Log entry 2", result);

        // Удаляем тестовый файл
        Files.delete(testLogFile);
    }

    @Test
    void testGetLogsForDate_NoLogsFound() {
        // Создаем тестовый файл с логами
        Path testLogFile = Path.of("test-logs.txt");
        try {
            Files.write(testLogFile, Arrays.asList(
                    "2023-04-28 Log entry 1",
                    "2023-04-28 Log entry 2"
            ));

            WrongRequest thrown = assertThrows(WrongRequest.class, () -> {
                logService.getLogsForDate(LocalDate.of(2023, 4, 29));
            });
            assertEquals("No logs found for date: 2023-04-29", thrown.getMessage());

        } catch (IOException e) {
            fail("IOException should not occur: " + e.getMessage());
        } finally {
            // Удаляем тестовый файл
            try {
                Files.deleteIfExists(testLogFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testGetLogsForDate_FileDoesNotExist() {
        logService.setLogFilePath("nonexistent-file.txt");

        IOException thrown = assertThrows(IOException.class, () -> {
            logService.getLogsForDate(LocalDate.of(2023, 4, 28));
        });

        assertEquals("Log file does not exist", thrown.getMessage());
    }

    @Test
    void testGetLogsForDate_EmptyFile() throws IOException {
        // Создаем пустой файл
        Path testLogFile = Path.of("test-logs.txt");
        Files.write(testLogFile, Collections.emptyList());

        WrongRequest thrown = assertThrows(WrongRequest.class, () -> {
            logService.getLogsForDate(LocalDate.of(2023, 4, 28));
        });
        assertEquals("No logs found for date: 2023-04-28", thrown.getMessage());

        // Удаляем тестовый файл
        Files.delete(testLogFile);
    }

    @Test
    void testGetLogsForDate_SingleEntry() throws IOException {
        // Создаем файл с одной записью
        Path testLogFile = Path.of("test-logs.txt");
        Files.write(testLogFile, Collections.singletonList("2023-04-28 Log entry"));

        String result = logService.getLogsForDate(LocalDate.of(2023, 4, 28));
        assertEquals("2023-04-28 Log entry", result);

        // Удаляем тестовый файл
        Files.delete(testLogFile);
    }

    @Test
    void testGetLogsForDate_MultipleEntriesDifferentDates() throws IOException {
        // Создаем файл с несколькими записями
        Path testLogFile = Path.of("test-logs.txt");
        Files.write(testLogFile, Arrays.asList(
                "2023-04-28 Log entry 1",
                "2023-04-29 Log entry 2",
                "2023-04-28 Log entry 3"
        ));

        String result = logService.getLogsForDate(LocalDate.of(2023, 4, 28));
        assertEquals("2023-04-28 Log entry 1\n2023-04-28 Log entry 3", result);

        // Удаляем тестовый файл
        Files.delete(testLogFile);
    }

    @Test
    void testGetLogsForDate_IncorrectDateFormat() {
        // Проверяем, что метод выбрасывает исключение для неверного формата файла
        assertThrows(IOException.class, () -> {
            logService.getLogsForDate(LocalDate.of(2023, 4, 31)); // Неверная дата
        });
    }
}