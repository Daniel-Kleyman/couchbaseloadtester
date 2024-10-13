package kleyman.it;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonObject;
import kleyman.config.CouchbaseConnectionManager;
import kleyman.metrics.CouchbaseMetrics;
import kleyman.metrics.MetricsSetup;
import kleyman.service.CouchbaseService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CouchbaseServiceIntegrationTest {

    private static CouchbaseConnectionManager connectionManager;
    private static CouchbaseService couchbaseService;
    private final CouchbaseMetrics couchbaseMetrics = new CouchbaseMetrics(MetricsSetup.getPrometheusRegistry(), " ", 1, " ", true);


    @BeforeAll
    static void setUp() {
        connectionManager = new CouchbaseConnectionManager(0);
        couchbaseService = new CouchbaseService(connectionManager);
        connectionManager.initializeBucket();
    }

    @Test
    @DisplayName("Test insert document successfully when given valid key and jsonData")
    public void givenValidIdAndContent_whenInsertDocument_thenSuccess() {
        // Given
        String key = "test-upload-doc";
        JsonObject jsonData = JsonObject.create().put("name", "Integration Test");

        // When
        couchbaseService.upload(key, jsonData, couchbaseMetrics);
        JsonObject retrievedData = couchbaseService.retrieve(key, couchbaseMetrics);

        // Then
        assertNotNull(retrievedData);
        assertEquals(jsonData, retrievedData);
    }

    @Test
    @DisplayName("Test retrieve document successfully when given valid key")
    public void givenValidId_whenRetrieveDocument_thenSuccess() {
        // Given
        String key = "test-retrieve-doc";
        JsonObject jsonData = JsonObject.create().put("name", "Integration Test");
        couchbaseService.upload(key, jsonData, couchbaseMetrics);

        // When
        JsonObject retrievedData = couchbaseService.retrieve(key, couchbaseMetrics);

        // Then
        assertNotNull(retrievedData);
        assertEquals(jsonData, retrievedData);
    }

    @Test
    @DisplayName("Test upload throws CouchbaseException for invalid document")
    public void givenInvalidId_whenUpload_thenThrowsCouchbaseException() {
        // Given
        String invalidKey = "";
        JsonObject jsonData = JsonObject.create().put("name", "Integration Test");

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            couchbaseService.upload(invalidKey, jsonData, couchbaseMetrics);
        });

        assertInstanceOf(CouchbaseException.class, exception);
    }

    @Test
    @DisplayName("Test retrieve throws CouchbaseException when document not found")
    public void givenInvalidKey_whenRetrieve_thenThrowsCouchbaseException() {
        // Given
        String key = "non-existing-doc";

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            couchbaseService.retrieve(key, couchbaseMetrics);
        });

        assertInstanceOf(CouchbaseException.class, exception, "Expected CouchbaseException to be thrown");
    }

    @AfterAll
    static void tearDown() {
        if (connectionManager != null) {
            connectionManager.close();
        }
    }
}
