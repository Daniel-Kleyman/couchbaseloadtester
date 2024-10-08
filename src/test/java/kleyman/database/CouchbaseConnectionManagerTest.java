package kleyman.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.kv.GetResult;
import kleyman.config.CouchbaseConnectionManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CouchbaseConnectionManagerTest {

    private static CouchbaseConnectionManager connectionManager;
    private static Collection collection;

    @BeforeAll
    static void setUp() {
        connectionManager = new CouchbaseConnectionManager(0);
        collection = connectionManager.getCollection();
    }

    @Test
    @DisplayName("Test insert document successfully when given valid ID and content")
    public void givenValidIdAndContent_whenInsertDocument_thenSuccess() {
        // Given
        String documentId = "test-doc-1";
        String content = "{\"name\": \"Integration Test\"}";

        // When
        MutationResult insertResult = collection.upsert(documentId, content);

        // Then
        assertNotNull(insertResult);
    }

    @Test
    @DisplayName("Test retrieve document successfully when given a valid ID")
    public void givenValidId_whenRetrieveDocument_thenSuccess() {
        // Given
        String documentId = "test-doc-1";
        String content = "{\"name\": \"Integration Test\"}";

        // When
        collection.upsert(documentId, content);
        GetResult getResult = collection.get(documentId);

        // Then
        assertNotNull(getResult);
        assertEquals(content, getResult.contentAs(String.class));
    }

    @Test
    @DisplayName("Test initialize the bucket successfully")
    public void givenInitializedConnection_whenInitializeBucket_thenSuccess() {
        // When & Then
        assertDoesNotThrow(() -> connectionManager.initializeBucket());
    }

    @Test
    @DisplayName("Test connect to the Couchbase cluster successfully")
    public void givenConnectionManager_whenConnectToCluster_thenSuccess() {
        // When
        Cluster cluster = connectionManager.getCluster();

        // Then
        assertNotNull(cluster);
    }

    @AfterAll
    static void tearDown() {
        if (connectionManager != null) {
            connectionManager.close();
        }
    }
}
