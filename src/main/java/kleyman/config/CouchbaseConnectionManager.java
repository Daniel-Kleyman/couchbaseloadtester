package kleyman.config;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import kleyman.util.EnvironmentVariableUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the connection to a Couchbase database.
 */
public class CouchbaseConnectionManager implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseConnectionManager.class);
    private final Cluster cluster;
    @Getter
    private final Collection collection;

    /**
     * Initializes a new CouchbaseConnectionManager by connecting to the Couchbase cluster
     * using credentials and bucket name from environment variables.
     */
    public CouchbaseConnectionManager() {
        String host = EnvironmentVariableUtils.getEnv("COUCHBASE_HOST");
        String username = EnvironmentVariableUtils.getEnv("COUCHBASE_USERNAME");
        String password = EnvironmentVariableUtils.getEnv("COUCHBASE_PASSWORD");
        String bucketName = EnvironmentVariableUtils.getEnv("COUCHBASE_BUCKET_NAME");

        try {
            cluster = Cluster.connect(host, username, password);
            Bucket bucket = cluster.bucket(bucketName);
            collection = bucket.defaultCollection();
            logger.info("Successfully connected to Couchbase bucket: {}", bucketName);
        } catch (Exception e) {
            logger.error("Failed to connect to Couchbase cluster: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to connect to Couchbase", e);
        }
    }

    @Override
    public void close() {
        if (cluster != null) {
            cluster.disconnect();
            logger.info("Couchbase connection closed.");
        }
    }

    /**
     * Ensures the Couchbase bucket is opened by performing an initial operation.
     * This should be called before starting load test threads to avoid the lazy bucket opening during the test.
     */
    public void initializeBucket() {
        try {
            // Attempt to retrieve a non-existent key (or any other simple operation)
            GetResult result = collection.get("initialization_check_key");
            logger.info("Bucket opened and initialized successfully.");
        } catch (CouchbaseException e) {
            logger.info("Bucket opened (non-existent key returned).");
        } catch (Exception e) {
            logger.error("Unexpected error during bucket initialization: {}", e.getMessage(), e);
            throw new RuntimeException("Error during bucket initialization", e);
        }
    }
}
