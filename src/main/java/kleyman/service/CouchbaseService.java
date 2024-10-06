package kleyman.service;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonObject;
import kleyman.config.CouchbaseConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for interacting with Couchbase database.
 */
public class CouchbaseService implements DataBaseService<JsonObject> {
    private final CouchbaseConnectionManager connectionManager;
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseService.class);

    /**
     * Constructs a CouchbaseService with the specified connection manager.
     */
    public CouchbaseService(CouchbaseConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void upload(String key, JsonObject jsonData) {
        try {
            connectionManager.getCollection().upsert(key, jsonData);
            logger.debug("Successfully inserted JSON document with key: {}", key);
        } catch (CouchbaseException e) {
            logger.error("Couchbase error inserting JSON document with key: {}", key, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error inserting JSON document with key: {}", key, e);
            throw new RuntimeException("Unexpected error inserting document with key: " + key, e);
        }
    }

    @Override
    public JsonObject retrieve(String key) {
        try {
            JsonObject jsonObject = connectionManager.getCollection().get(key).contentAs(JsonObject.class);
            if (jsonObject == null) {
                throw new CouchbaseException("Document not found for key: " + key);
            }
            logger.debug("Successfully retrieved JSON document with ID: {}", key);
            return jsonObject;
        } catch (CouchbaseException e) {
            logger.error("Couchbase error retrieving JSON document with key: {}", key, e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error retrieving JSON document with key: {}", key, e);
            throw new RuntimeException("Unexpected error retrieving document with key: " + key, e);
        }
    }

    public void retrieveJsonThreeTimes(String key) {
        for (int i = 0; i < 3; i++) {
            try {
                JsonObject jsonResult = retrieve(key);
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