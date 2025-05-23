package com.example.fakemaleru.service;

import java.util.Map;

public interface VisitorCounterService {

    long registerVisit(String url);

    long getVisitCount(String url);

    long getTotalVisitCount();

    Map<String, Long> getAllStats();
}