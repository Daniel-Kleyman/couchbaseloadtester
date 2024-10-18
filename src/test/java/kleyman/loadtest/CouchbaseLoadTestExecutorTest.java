package kleyman.loadtest;

import com.couchbase.client.java.json.JsonObject;
import kleyman.service.CouchbaseService;
import kleyman.util.JsonFileReaderUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouchbaseLoadTestExecutorTest {
    private CouchbaseLoadTestExecutor loadTestExecutor;
    private CouchbaseService couchbaseService;

    @BeforeEach
    void setUp() {
        couchbaseService = mock(CouchbaseService.class);
        loadTestExecutor = new CouchbaseLoadTestExecutor(5, "path/to/json", true, couchbaseService, "scenario12");
    }

    @Test
    @DisplayName("Test load test executor initialization")
    void GivenLoadTestExecutor_WhenInitialized_ThenPropertiesAreSetCorrectly() {
        // Given
        // Load test executor is initialized

        // When
        // Access properties

        // Then
        assertEquals(5, loadTestExecutor.getThreadCount());
        assertTrue(loadTestExecutor.isUseUniqueKeys());

    }

    @Test
    @DisplayName("Test key creation for unique keys")
    void GivenThreadId_WhenCreatingKeys_ThenReturnsUniqueKey() {
        // Given
        int threadId = 1;

        // When
        String key = loadTestExecutor.createUniqueKey(threadId);

        // Then
        assertTrue(key.startsWith("user::1::"));
    }

    @Test
    @DisplayName("Test key creation for shared keys")
    void GivenSharedKeySetting_WhenCreatingKeys_ThenReturnsSharedKey() {
        // Given
        int threadId = 1;
        loadTestExecutor = new CouchbaseLoadTestExecutor(5, "path/to/json", false, couchbaseService, "scenario12");

        // When
        String key = loadTestExecutor.createUniqueKey(threadId);

        // Then
        assertEquals("user::shared", key);
    }

    @Test
    @DisplayName("Test JSON data loading from file")
    void GivenJsonFilePath_WhenLoadJsonData_ThenJsonObjectIsReturned() throws Exception {
        // Given
        String jsonFilePath = "src/test/resources/validJson.json";
        JsonObject expectedJson = JsonObject.create().put("key", "value");

        // Mock static method
        mockStatic(JsonFileReaderUtils.class);
        when(JsonFileReaderUtils.readJsonFromFile(jsonFilePath)).thenReturn(expectedJson);

        // Use reflection to access the private method
        Method loadJsonDataMethod = CouchbaseLoadTestExecutor.class.getDeclaredMethod("loadJsonDataFromFile", String.class);
        loadJsonDataMethod.setAccessible(true);

        // When
        JsonObject actualJson = (JsonObject) loadJsonDataMethod.invoke(loadTestExecutor, jsonFilePath);

        // Then
        assertEquals(expectedJson, actualJson);
    }
}
