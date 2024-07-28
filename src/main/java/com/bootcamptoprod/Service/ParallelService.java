package com.bootcamptoprod.Service;


import com.bootcamptoprod.mock.dto.MockResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

@Service
public class ParallelService {

    private RestTemplate restTemplate;

    public ParallelService() {
        this.restTemplate = new RestTemplate();
    }

    private final Executor executor = Executors.newFixedThreadPool(10);

    // Fetches data from a given URL asynchronously
    public CompletableFuture<MockResponse> fetchData(String url) {
        return CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url, MockResponse.class), executor);
    }

    // Fetches data from multiple URLs in parallel and aggregates the results
    public CompletableFuture<List<MockResponse>> fetchAllData() {
        CompletableFuture<MockResponse> call1 = fetchData("http://localhost:8080/mock/data/1");
        CompletableFuture<MockResponse> call2 = fetchData("http://localhost:8080/mock/data/2");
        CompletableFuture<MockResponse> call3 = fetchData("http://localhost:8080/mock/data/3");

        return CompletableFuture.allOf(call1, call2, call3)
                .thenApply(v -> Stream.of(call1, call2, call3)
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .toList());
    }

    // Fetches data from multiple URLs in parallel with a global timeout and aggregates the results
    public CompletableFuture<List<MockResponse>> fetchAllDataWithTimeout(long globalTimeout, long individualTaskTimeout) {
        CompletableFuture<MockResponse> call1 = fetchDataWithTimeout("http://localhost:8080/mock/data/1", individualTaskTimeout);
        CompletableFuture<MockResponse> call2 = fetchDataWithTimeout("http://localhost:8080/mock/data/2", individualTaskTimeout);
        CompletableFuture<MockResponse> call3 = fetchDataWithTimeout("http://localhost:8080/mock/data/3", individualTaskTimeout);

        return CompletableFuture.allOf(call1, call2, call3)
                .orTimeout(globalTimeout, TimeUnit.MILLISECONDS)
                .handle((result, ex) -> {
                    if (ex != null) {
                        if (ex instanceof TimeoutException) {
                            System.err.println("Global timeout exception encountered");
                        } else {
                            System.err.println("Some other exception encountered at global level");
                        }
                        return Collections.emptyList();
                    }
                    return Stream.of(call1, call2, call3)
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull)
                            .toList();
                });
    }

    // Fetches data asynchronously with a specified timeout
    public CompletableFuture<MockResponse> fetchDataWithTimeout(String url, long timeout) {
        CompletableFuture<MockResponse> future = CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url, MockResponse.class), executor);
        return future.orTimeout(timeout, TimeUnit.MILLISECONDS).exceptionally(ex -> {
            if (ex instanceof TimeoutException) {
                System.err.println("Timeout while fetching data from " + url);
            } else {
                System.err.println("Some other exception encountered while fetching data from " + url);
            }
            return null;
        });
    }

    public CompletableFuture<MockResponse> fetchDataWithHandling(String url) {
        return CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url, MockResponse.class), executor)
                .handle((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Error fetching data from " + url + ": " + ex.getMessage());
                        return null;
                    }
                    return result;
                });
    }

    // Fetches data from multiple URLs in parallel and aggregates the results.
    // One call with return null because error will be encountered in that call and as a fallback that will return null value
    public CompletableFuture<List<MockResponse>> fetchAllDataWithErrorInOneCall() {
        CompletableFuture<MockResponse> call1 = fetchDataWithHandling("http://localhost:8080/mock/data/1");
        CompletableFuture<MockResponse> call2 = fetchDataWithHandling("http://localhost:8080/mock/data/2/exception");
        CompletableFuture<MockResponse> call3 = fetchDataWithHandling("http://localhost:8080/mock/data/3");

        return CompletableFuture.allOf(call1, call2, call3)
                .thenApply(v -> Stream.of(call1, call2, call3)
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .toList());
    }

}