package com.example.fakemaleru.service.impl;

import com.example.fakemaleru.service.VisitorCounterService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;


@Service
public class VisitorCounterServiceImpl implements VisitorCounterService {

    private final Map<String, Long> urlCounters = new ConcurrentHashMap<>();

    private long totalCounter = 0;

    public synchronized long registerVisit(String url) {
        totalCounter++;

        Long count = urlCounters.getOrDefault(url, 0L);
        long newCount = count + 1L; // Увеличиваем счетчик
        urlCounters.put(url, newCount); // Обновляем значение в мапе
        System.out.println("Registering visit for URL: " + url + ", new count: " + newCount);
        return newCount; // Возвращаем новое значение
    }

    public synchronized long getVisitCount(String url) {
        Long counter = urlCounters.get(url);
        return counter != null ? counter : 0;
    }

    public synchronized long getTotalVisitCount() {
        return totalCounter;
    }

    public synchronized Map<String, Long> getAllStats() {
        return new ConcurrentHashMap<>(urlCounters);
    }
}