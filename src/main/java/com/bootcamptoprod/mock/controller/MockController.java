package com.bootcamptoprod.mock.controller;

import com.bootcamptoprod.mock.dto.MockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mock")
public class MockController {

    private static final Logger logger = LoggerFactory.getLogger(MockController.class);

    // Simulates mock respose with delay
    @GetMapping("/data/{id}")
    public MockResponse getMockData(@PathVariable String id) throws InterruptedException {
        logger.info("Received a request for mock data with id: {}", id);

        Thread.sleep(3000); // Adding a delay to simulate real-world scenarios

        MockResponse response = new MockResponse();
        response.setId(id);
        response.setMessage("Mock data for ID " + id);
        logger.info("Response sent for mock data with id: {}", id);
        return response;
    }

    // Simulates exception response
    @GetMapping("/data/{id}/exception")
    public MockResponse getMockDataException(@PathVariable String id) {
        logger.info("Received a request for mock data with id: {}", id);

        throw new RuntimeException("Something went wrong");
    }
}
