package kleyman.service;

import com.couchbase.client.java.json.JsonObject;
import kleyman.config.CouchbaseConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for interacting with Couchbase database.
 */
public class CouchbaseServiceImpl {
    private final CouchbaseConnectionManager connectionManager;
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseServiceImpl.class);

    /**
     * Constructs a CouchbaseService with the specified connection manager.
     */
    public CouchbaseServiceImpl() {
        this.connectionManager = new CouchbaseConnectionManager();
    }

    /**
     * Inserts a JSON document into Couchbase by key.
     *
     * @param key      the key under which to store the JSON document
     * @param jsonData the JSON object to insert
     */
    public void insertJson(String key, JsonObject jsonData) {
        try {
            connectionManager.getCollection().upsert(key, jsonData);
            logger.info("Successfully inserted JSON document with key: {}", key);
        } catch (Exception e) {
            logger.error("Error inserting JSON document with key: {}", key, e);
            throw new RuntimeException("Failed to insert JSON document", e);
        }
    }

    /**
     * Retrieves a JSON document from Couchbase by key.
     *
     * @param documentId the ID of the document to retrieve
     * @return the retrieved JSON object
     */
    public JsonObject retrieveJson(String documentId) {
        try {
            JsonObject jsonObject = connectionManager.getCollection().get(documentId).contentAs(JsonObject.class);
            logger.info("Successfully retrieved JSON document with ID: {}", documentId);
            return jsonObject;
        } catch (Exception e) {
            logger.error("Error retrieving JSON document with ID: {}", documentId, e);
            throw new RuntimeException("Failed to retrieve JSON document", e);
        }
    }

    /**
     * Retrieves the specified JSON document from Couchbase three times.
     *
     * @param key the key of the document to retrieve
     */
    public void retrieveJsonThreeTimes(String key) {
        for (int i = 0; i < 3; i++) {
            try {
                JsonObject jsonResult = retrieveJson(key);
                // Optional: Log or process jsonResult if needed
                logger.debug("Retrieved JSON result: {}", jsonResult);
            } catch (Exception e) {
                logger.error("Error retrieving JSON document on attempt {} for key: {}", i + 1, key, e);
            }
        }
    }

    public void closeConnection() {
        connectionManager.closeConnection();
    }
}
