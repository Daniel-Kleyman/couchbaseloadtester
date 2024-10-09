package kleyman.it;

import com.couchbase.client.java.json.JsonObject;
import kleyman.config.CouchbaseConnectionManager;
import kleyman.loadtest.CouchbaseLoadTestExecutor;
import kleyman.service.CouchbaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CouchbaseTestRunnerIntegrationTest {

    @Test
    @DisplayName("Test runLoadTest scenario uploads and retrieves data")
    void GivenValidCouchbaseService_WhenRunLoadTest_ThenDataUploadedAndRetrieved() throws Exception {
        // Given
        CouchbaseConnectionManager connectionManager = new CouchbaseConnectionManager(0);
        connectionManager.initializeBucket();
        CouchbaseService couchbaseService = new CouchbaseService(connectionManager);
        CouchbaseLoadTestExecutor loadTestExecutor = new CouchbaseLoadTestExecutor(1, "src/test/resources/testData/testData.json", true, couchbaseService);

        // When
        loadTestExecutor.executeLoadTest();

        String key = "user::1::" + System.nanoTime();
        JsonObject jsonData = JsonObject.create()
                .put("name", "test")
                .put("value", 123);

        couchbaseService.upload(key, jsonData);
        JsonObject retrievedData = couchbaseService.retrieve(key);

        // Then
        assertNotNull(retrievedData);
        assertEquals(jsonData, retrievedData);
    }
}