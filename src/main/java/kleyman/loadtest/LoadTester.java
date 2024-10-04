package kleyman.loadtest;

import com.couchbase.client.java.json.JsonObject;
import kleyman.service.CouchbaseServiceImpl;
import kleyman.util.JsonFileReader;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LoadTester {
    private final CouchbaseServiceImpl couchbaseService;
    private final long testDurationMillis = TimeUnit.MINUTES.toMillis(3);

    public LoadTester(CouchbaseServiceImpl couchbaseService) {
        this.couchbaseService = couchbaseService;
    }

    // Method to run the load test with multiple threads
    public void runLoadTest(int numberOfThreads, String jsonFilePath, boolean useUniqueKeys) throws IOException {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        JsonObject jsonData = JsonFileReader.readJsonFromFile(jsonFilePath); // Using JsonFileReader to read the JSON file

        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> runThreadOperations(threadId, jsonData, useUniqueKeys));
        }

        shutdownExecutor(executor);

    }

    // Operations for each thread: Insert JSON and retrieve it three times
    private void runThreadOperations(int threadId, JsonObject jsonData, boolean useUniqueKeys) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < testDurationMillis) {
            String key = generateKey(useUniqueKeys, threadId);
            couchbaseService.insertJson(key, jsonData);  // Insert JSON into Couchbase
            couchbaseService.retrieveJsonThreeTimes(key); // Retrieve JSON three times
        }
    }

    // Generate a key, either unique or shared
    private String generateKey(boolean useUniqueKeys, int threadId) {
        return useUniqueKeys ? "user::" + threadId + "::" + System.nanoTime() : "user::shared";
    }

    // Shutdown the executor service
    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            executor.awaitTermination(3, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
