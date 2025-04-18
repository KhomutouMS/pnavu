package com.example.fakemaleru.service;

import java.io.IOException;
import java.time.LocalDate;

public interface LogService {
    String getLogsForDate(LocalDate date) throws IOException;
}