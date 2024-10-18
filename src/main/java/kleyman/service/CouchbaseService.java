package kleyman.service;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonObject;
import kleyman.config.CouchbaseConnectionManager;
import kleyman.metrics.CouchbaseMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Service class for interacting with Couchbase database.
 */
public class CouchbaseService implements DataBaseService<JsonObject, CouchbaseMetrics> {
    private final CouchbaseConnectionManager connectionManager;
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseService.class);

    /**
     * Constructs a CouchbaseService with the specified connection manager.
     */
    public CouchbaseService(CouchbaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    /**
     * Uploads a JSON document to Couchbase using the specified key.
     * The method also tracks metrics for operation success, failure, and latency.
     */
    @Override
    public void upload(String key, JsonObject jsonData, CouchbaseMetrics couchbaseMetrics) {
        long startTime = System.nanoTime();
        try {
            connectionManager.getCollection().upsert(key, jsonData);
            logger.debug("Successfully inserted JSON document with key: {}", key);
            couchbaseMetrics.incrementPutSuccess();
        } catch (CouchbaseException e) {
            logger.error("Couchbase error inserting JSON document with key: {}", key, e);
            couchbaseMetrics.incrementPutFailure();
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error inserting JSON document with key: {}", key, e);
            couchbaseMetrics.incrementPutFailure();
            throw new RuntimeException("Unexpected error inserting document with key: " + key, e);
        } finally {
            long duration = System.nanoTime() - startTime;
            couchbaseMetrics.recordPutLatency(duration, TimeUnit.NANOSECONDS);
        }
    }

    /**
     * Retrieves a JSON document from Couchbase using the specified key.
     * The method also tracks metrics for operation success, failure, and latency.
     */
    @Override
    public JsonObject retrieve(String key, CouchbaseMetrics couchbaseMetrics) {
        long startTime = System.nanoTime();
        try {
            JsonObject jsonObject = connectionManager.getCollection().get(key).contentAs(JsonObject.class);
            if (jsonObject == null) {
                throw new CouchbaseException("Document not found for key: " + key);
            }
            logger.debug("Successfully retrieved JSON document with key: {}", key);
            couchbaseMetrics.incrementGetSuccess();
            return jsonObject;
        } catch (CouchbaseException e) {
            logger.error("Couchbase error retrieving JSON document with key: {}", key, e);
            couchbaseMetrics.incrementGetFailure();
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving JSON document with key: {}", key, e);
            couchbaseMetrics.incrementGetFailure();
            throw new RuntimeException("Unexpected error retrieving document with key: " + key, e);
        } finally {
            long duration = System.nanoTime() - startTime;
            couchbaseMetrics.recordGetLatency(duration, TimeUnit.NANOSECONDS);
        }
    }

    public void retrieveJsonThreeTimes(String key, CouchbaseMetrics couchbaseMetrics) {
        for (int i = 0; i < 3; i++) {
            try {
                JsonObject jsonResult = retrieve(key, couchbaseMetrics);
                logger.debug("Retrieved JSON result: {}", jsonResult);
            } catch (CouchbaseException e) {
                logger.error("Attempt {}: Couchbase error retrieving JSON document for key: {}", i + 1, key, e);
                throw e;
            } catch (Exception e) {
                logger.error("Attempt {}: Unexpected error for key: {}", i + 1, key, e);
                throw new RuntimeException("Unexpected error retrieving document with key: " + key, e);
            }
        }
    }
}