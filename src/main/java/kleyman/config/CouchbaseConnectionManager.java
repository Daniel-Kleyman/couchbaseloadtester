package kleyman.config;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;

public class CouchbaseConnectionManager {
    private final Cluster cluster;
    private final Bucket bucket;
    private final Collection collection;

    public CouchbaseConnectionManager() {
        // Fetch configuration details from environment variables
        String username = System.getenv("COUCHBASE_USERNAME");
        String password = System.getenv("COUCHBASE_PASSWORD");
        String bucketName = System.getenv("COUCHBASE_BUCKET_NAME");

        cluster = Cluster.connect("localhost", username, password);
        bucket = cluster.bucket(bucketName);
        collection = bucket.defaultCollection();
    }

    public Collection getCollection() {
        return collection;
    }

    public void closeConnection() {
        cluster.disconnect();
    }
}
