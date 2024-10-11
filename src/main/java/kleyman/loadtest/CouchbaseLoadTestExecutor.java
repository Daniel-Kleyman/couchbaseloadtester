package kleyman.loadtest;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonObject;
import kleyman.metrics.CouchbaseMetrics;
import kleyman.metrics.MetricsSetup;
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
public class CouchbaseLoadTestExecutor implements LoadTestExecutor {
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseLoadTestExecutor.class);
    @Getter
    private final int threadCount;
    @Getter
    private final boolean useUniqueKeys;
    private final String jsonFilePath;
    private final long testDurationMillis;
    private final CouchbaseService couchbaseService;
    private final CouchbaseMetrics couchbaseMetrics;

    /**
     * Constructs a CouchbaseTestScenario for running load tests.
     *
     * @param threadCount      number of concurrent threads to use
     * @param jsonFilePath     file path to the JSON data
     * @param useUniqueKeys    whether to use unique keys for each operation
     * @param couchbaseService the service to interact with the Couchbase database
     * @param scenarioId       scenario id
     */
    public CouchbaseLoadTestExecutor(int threadCount, String jsonFilePath, boolean useUniqueKeys, CouchbaseService couchbaseService, String scenarioId) {
        this.threadCount = threadCount;
        this.jsonFilePath = jsonFilePath;
        this.useUniqueKeys = useUniqueKeys;
        this.couchbaseService = couchbaseService;
        this.testDurationMillis = Long.parseLong(System.getProperty("load.test.duration.millis", "1500"));
        this.couchbaseMetrics = new CouchbaseMetrics(MetricsSetup.getRegistry(), scenarioId);
    }

    /**
     * Starts the load test by initializing the executor service and running multiple threads.
     * Each thread will perform upload of JSON file from file system and retrieval operations
     * based on the specified configuration.
     */

    @Override
    public void executeLoadTest() {
        logger.info("Starting load test with {} threads using unique keys: {}", threadCount, useUniqueKeys);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 1; i <= threadCount; i++) {
            JsonObject jsonData = loadJsonDataFromFile(jsonFilePath + i + ".json");
            if (jsonData == null) return;
            final int threadId = i;
            executor.submit(() -> performThreadOperations(threadId, jsonData));
        }
        shutdownExecutor(executor);
        logger.info("Load test completed.");
    }

    private JsonObject loadJsonDataFromFile(String jsonFilePathForThread) {
        logger.info("Loading JSON data from file: {}", jsonFilePathForThread);
        try {
            return JsonFileReaderUtils.readJsonFromFile(jsonFilePathForThread);
        } catch (IOException e) {
            logger.error("Failed to read JSON data from file: {}", jsonFilePathForThread, e);
            return null;
        }
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
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime <= testDurationMillis) {
            String key = createKeyKey(threadId);
            try {
                couchbaseService.upload(key, jsonData, couchbaseMetrics);
                logger.debug("Thread {}: Uploaded data for key: {}", threadId, key);
                couchbaseService.retrieveJsonThreeTimes(key, couchbaseMetrics);
                logger.debug("Thread {}: Uploaded and retrieved data for key: {}", threadId, key);
            } catch (CouchbaseException e) {
                logger.error("Thread {}: Couchbase error during operations for key: {}", threadId, key, e);
            } catch (Exception e) {
                logger.error("Thread {}: Unexpected error during operations for key: {}", threadId, key, e);
            }
        }
        logger.info("Thread {} completed operations.", threadId);
    }

    String createKeyKey(int threadId) {
        return useUniqueKeys ? "user::" + threadId + "::" + System.nanoTime() : "user::shared";
    }

    /**
     * Shuts down the ExecutorService and waits for tasks to complete.
     *
     * @param executor the ExecutorService to shut down
     */
    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            boolean terminated = executor.awaitTermination(60, TimeUnit.MINUTES);
            if (!terminated) {
                logger.warn("Executor did not terminate in the specified time.");
            } else {
                logger.info("Executor terminated successfully.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Executor service interrupted during shutdown.", e);
        }
    }

}
