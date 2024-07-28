package com.bootcamptoprod.controller;

import com.bootcamptoprod.Service.ParallelService;
import com.bootcamptoprod.mock.dto.MockResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class ParallelController {

    private static final Logger logger = LoggerFactory.getLogger(ParallelController.class);

    @Autowired
    private ParallelService parallelService;

    // Endpoint to trigger single data fetching
    @GetMapping("/fetchData")
    public CompletableFuture<MockResponse> getData() {
        logger.info("Request received for 'fetchData' endpoint.");

        CompletableFuture<MockResponse> responseCompletableFuture = parallelService.fetchData("http://localhost:8080/mock/data/1");

        logger.info("Request processing completed for 'fetchData' endpoint.");
        return responseCompletableFuture;
    }

    // Endpoint to trigger parallel data fetching
    @GetMapping("/parallel")
    public CompletableFuture<List<MockResponse>> getParallelData() {
        logger.info("Request received for 'parallel' endpoint.");

        CompletableFuture<List<MockResponse>> listCompletableFuture = parallelService.fetchAllData();

        logger.info("Request processing completed for 'parallel' endpoint.");
        return listCompletableFuture;
    }

    // Endpoint to trigger parallel data fetching with exception in one task
    @GetMapping("/parallelWithExceptionIOneTask")
    public CompletableFuture<List<MockResponse>> getParallelWithExceptionIOneTask() {
        logger.info("Request received for 'parallelWithExceptionIOneTask' endpoint.");

        CompletableFuture<List<MockResponse>> listCompletableFuture = parallelService.fetchAllDataWithErrorInOneCall();

        logger.info("Request processing completed for 'parallelWithExceptionIOneTask' endpoint.");
        return listCompletableFuture;
    }

    // Endpoint to trigger parallel data fetching with a global timeout
    @GetMapping("/parallelWithGlobalTimeout")
    public CompletableFuture<List<MockResponse>> getParallelDataWithGlobalTimeout() {
        logger.info("Request received for 'parallelWithTimeout' endpoint.");

        CompletableFuture<List<MockResponse>> listCompletableFuture = parallelService.fetchAllDataWithTimeout(1000, 5000);

        logger.info("Request processing completed for 'parallelWithTimeout' endpoint.");
        return listCompletableFuture;
    }

    // Endpoint to trigger parallel data fetching with individual task timeout
    @GetMapping("/parallelWithIndividualTaskTimeout")
    public CompletableFuture<List<MockResponse>> getParallelWithIndividualTaskTimeout() {
        logger.info("Request received for 'parallelWithIndividualTaskTimeout' endpoint.");

        CompletableFuture<List<MockResponse>> listCompletableFuture = parallelService.fetchAllDataWithTimeout(10000, 2000);

        logger.info("Request processing completed for 'parallelWithIndividualTaskTimeout' endpoint.");
        return listCompletableFuture;
    }

}
