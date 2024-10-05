package kleyman.config;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
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
}
