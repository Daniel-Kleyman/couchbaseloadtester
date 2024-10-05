package kleyman.loadtest;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonObject;
import kleyman.service.CouchbaseService;
import kleyman.util.JsonFileReaderUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Couchbase implementation of the LoadTestExecutor interface for conducting load tests.
 */
public class CouchbaseLoadTestExecutor implements LoadTestExecutor<JsonObject> {
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseLoadTestExecutor.class); // Logger instance
    @Getter
    private final int threadCount;
    @Getter
    private final boolean useUniqueKeys;
    private final String jsonFilePath;
    private final long testDurationMillis = TimeUnit.MINUTES.toMillis(3);
    private final CouchbaseService couchbaseService;

    /**
     * Constructs a CouchbaseTestScenario for running load tests.
     *
     * @param threadCount      number of concurrent threads to use
     * @param jsonFilePath     file path to the JSON data
     * @param useUniqueKeys    whether to use unique keys for each operation
     * @param couchbaseService the service to interact with the Couchbase database
     */
    public CouchbaseLoadTestExecutor(int threadCount, String jsonFilePath, boolean useUniqueKeys, CouchbaseService couchbaseService) {
        this.threadCount = threadCount;
        this.jsonFilePath = jsonFilePath;
        this.useUniqueKeys = useUniqueKeys;
        this.couchbaseService = couchbaseService;
    }

    /**
     * Starts the load test by initializing the executor service and running multiple threads.
     * Each thread will perform upload and retrieval operations based on the specified configuration.
     */
    @Override
    public void executeLoadTest() {
        logger.info("Starting load test with {} threads using unique keys: {}", threadCount, useUniqueKeys);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        JsonObject jsonData;
        try {
            jsonData = JsonFileReaderUtils.readJsonFromFile(jsonFilePath);
        } catch (IOException e) {
            logger.error("Failed to read JSON data from file: {}", jsonFilePath, e);
            return;
        }
        for (int i = 1; i <= threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> performThreadOperations(threadId, jsonData));
        }
        shutdownExecutor(executor);
        logger.info("Load test completed.");
    }

    /**
     * Executes the operations for a specific thread.
     * Each thread uploads data to the Couchbase database and retrieves it multiple times within the test duration.
     *
     * @param threadId the identifier for the current thread
     * @param jsonData the JSON data to be uploaded and retrieved
     */
    private void performThreadOperations(int threadId, JsonObject jsonData) {
        logger.info("Thread {} starting operations.", threadId);
        String key = createKeyKey(threadId);
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime <= testDurationMillis) {
            try {
                couchbaseService.upload(key, jsonData);
                logger.debug("Thread {}: Uploaded data for key: {}", threadId, key);
                couchbaseService.retrieveJsonThreeTimes(key);
                logger.debug("Thread {}: Uploaded and retrieved data for key: {}", threadId, key);
            } catch (CouchbaseException e) {
                logger.error("Thread {}: Couchbase error during operations for key: {}", threadId, key, e);
            } catch (Exception e) {
                logger.error("Thread {}: Unexpected error during operations for key: {}", threadId, key, e);
            }
        }
        logger.info("Thread {} completed operations.", threadId);
    }

    public String createKeyKey(int threadId) {
        return useUniqueKeys ? "user::" + threadId + "::" + System.nanoTime() : "user::shared";
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            executor.awaitTermination(3, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Executor service interrupted during shutdown.", e);
        }
    }
}
