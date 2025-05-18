package com.example.fakemaleru.service;

import com.example.fakemaleru.service.impl.VisitorCounterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class VisitorCounterServiceImplTest {

    private VisitorCounterServiceImpl visitorCounterService;

    @BeforeEach
    public void setUp() {
        visitorCounterService = new VisitorCounterServiceImpl();
    }

    @Test
    public void testRegisterVisit() {
        String url = "http://example.com";
        long firstCount = visitorCounterService.registerVisit(url);
        assertEquals(1, firstCount, "First visit should return count 1");

        long secondCount = visitorCounterService.registerVisit(url);
        assertEquals(2, secondCount, "Second visit should return count 2");

        long totalCount = visitorCounterService.getVisitCount(url);
        assertEquals(2, totalCount, "Total visit count for URL should be 2");
    }

    @Test
    public void testGetVisitCount() {
        String url = "http://example.com";
        visitorCounterService.registerVisit(url);
        visitorCounterService.registerVisit(url);

        long count = visitorCounterService.getVisitCount(url);
        assertEquals(2, count, "Visit count for the URL should be 2");
    }

    @Test
    public void testGetVisitCountForUnknownUrl() {
        String url = "http://unknown.com";
        long count = visitorCounterService.getVisitCount(url);
        assertEquals(0, count, "Visit count for an unknown URL should be 0");
    }

    @Test
    public void testGetTotalVisitCount() {
        visitorCounterService.registerVisit("http://example.com");
        visitorCounterService.registerVisit("http://example.com");
        visitorCounterService.registerVisit("http://another-example.com");

        long totalCount = visitorCounterService.getTotalVisitCount();
        assertEquals(3, totalCount, "Total visit count should be 3");
    }

    @Test
    public void testGetAllStats() {
        visitorCounterService.registerVisit("http://example.com");
        visitorCounterService.registerVisit("http://another-example.com");

        Map<String, Long> stats = visitorCounterService.getAllStats();
        assertEquals(2, stats.size(), "There should be 2 entries in the stats map");
        assertEquals(1, stats.get("http://example.com"), "Visit count for first URL should be 1");
        assertEquals(1, stats.get("http://another-example.com"), "Visit count for second URL should be 1");
    }
}