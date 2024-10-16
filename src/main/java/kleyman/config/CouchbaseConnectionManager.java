package kleyman.config;

import com.couchbase.client.core.env.IoConfig;
import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.ClusterOptions;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.env.ClusterEnvironment;
import com.couchbase.client.java.kv.GetResult;
import kleyman.util.EnvironmentVariableUtils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

/**
 * Manages the connection to a Couchbase database.
 */
public class CouchbaseConnectionManager implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseConnectionManager.class);
    @Getter
    private final Cluster cluster;
    @Getter
    private final Collection collection;
    private ClusterEnvironment env;

    /**
     * Initializes a new CouchbaseConnectionManager by connecting to the Couchbase cluster
     * using credentials and bucket name from environment variables and also connection pool size.
     * Note: "ioConfig" and "numKvConnections" are marked as "deprecated" in current Couchbase
     * documentation but still work fine.
     */
    public CouchbaseConnectionManager(int customPoolSize) {
        String host = EnvironmentVariableUtils.getEnv("COUCHBASE_HOST");
        String username = EnvironmentVariableUtils.getEnv("COUCHBASE_USERNAME");
        String password = EnvironmentVariableUtils.getEnv("COUCHBASE_PASSWORD");
        String bucketName = EnvironmentVariableUtils.getEnv("COUCHBASE_BUCKET_NAME");

        this.env = createClusterEnvironment(customPoolSize);
        this.cluster = connectToCluster(host, username, password);
        this.collection = initializeBucket(bucketName);
    }

    private ClusterEnvironment createClusterEnvironment(int customPoolSize) {
        if (customPoolSize > 0) {
            logger.info("Custom KV connection pool size set to: {}", customPoolSize);
            return ClusterEnvironment.builder()
                    .ioConfig(IoConfig.numKvConnections(customPoolSize))
                    .build();
        } else {
            logger.info("Using default KV connection pool size.");
            return null;
        }
    }

    private Cluster connectToCluster(String host, String username, String password) {
        try {
            if (env != null) {
                return Cluster.connect(host, ClusterOptions.clusterOptions(username, password).environment(env));
            } else {
                return Cluster.connect(host, username, password);
            }
        } catch (Exception e) {
            logger.error("Failed to connect to Couchbase cluster: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to connect to Couchbase", e);
        }
    }

    private Collection initializeBucket(String bucketName) {
        try {
            Bucket bucket = cluster.bucket(bucketName);
            logger.info("Successfully connected to Couchbase bucket: {}", bucketName);
            return bucket.defaultCollection();
        } catch (Exception e) {
            logger.error("Failed to initialize bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize bucket", e);
        }
    }

    @Override
    public void close() {
        if (cluster != null) {
            cluster.disconnect();
            if (env != null) {
                env.shutdown();
                logger.info("Couchbase environment shutdown.");
            }
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
