package kleyman.service;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import kleyman.config.CouchbaseConnectionManager;
import kleyman.metrics.CouchbaseMetrics;
import kleyman.metrics.MetricsSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CouchbaseServiceTest {
    private static final String TEST_KEY = "testKey";
    private static final String NON_EXISTENT_KEY = "nonExistentKey";
    private static final String CONNECTION_FAILED_MESSAGE = "Connection failed";
    private static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected error";
    private final CouchbaseMetrics couchbaseMetrics = new CouchbaseMetrics(MetricsSetup.getPrometheusRegistry(), " ", 1, " ", true);
    private CouchbaseConnectionManager connectionManager;
    private CouchbaseService couchbaseService;
    private JsonObject jsonData;

    @BeforeEach
    void setUp() {
        connectionManager = Mockito.mock(CouchbaseConnectionManager.class);
        couchbaseService = new CouchbaseService(connectionManager);
        jsonData = JsonObject.create().put("field", "value");

    }

    private com.couchbase.client.java.Collection createMockCollection() {
        return Mockito.mock(com.couchbase.client.java.Collection.class);
    }

    @Test
    @DisplayName("Test document insertion with valid JSON data")
    public void givenValidJson_whenInsertDocument_thenDocumentShouldBeInsertedSuccessfully() {
        // Given
        var mockCollection = createMockCollection();
        when(connectionManager.getCollection()).thenReturn(mockCollection);

        // When
        couchbaseService.upload(TEST_KEY, jsonData, couchbaseMetrics);

        // Then
        verify(mockCollection, times(1)).upsert(TEST_KEY, jsonData);
    }

    @Test
    @DisplayName("Test upload functionality throws CouchbaseException on failure")
    public void givenCouchbaseException_whenUpload_thenShouldThrowCouchbaseException() {
        // Given
        var mockCollection = createMockCollection();
        when(connectionManager.getCollection()).thenReturn(mockCollection);
        when(mockCollection.upsert(anyString(), any())).thenThrow(new CouchbaseException(CONNECTION_FAILED_MESSAGE));

        // When
        CouchbaseException thrownException = assertThrows(CouchbaseException.class, () -> couchbaseService.upload(TEST_KEY, jsonData, couchbaseMetrics));

        // Then
        assertEquals(CONNECTION_FAILED_MESSAGE, thrownException.getMessage());
    }

    @Test
    @DisplayName("Test upload functionality throws RuntimeException on unexpected errors")
    public void givenUnexpectedException_whenUpload_thenShouldThrowRuntimeException() {
        // Given
        var mockCollection = createMockCollection();
        when(connectionManager.getCollection()).thenReturn(mockCollection);
        when(mockCollection.upsert(anyString(), any())).thenThrow(new RuntimeException(UNEXPECTED_ERROR_MESSAGE));

        // When
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            couchbaseService.upload(TEST_KEY, jsonData, couchbaseMetrics);
        });

        // Then
        assertEquals("Unexpected error inserting document with key: " + TEST_KEY, thrownException.getMessage());
    }

    @Test
    @DisplayName("Should retrieve document successfully when given a valid key")
    public void givenValidKey_whenRetrieveDocument_thenDocumentShouldBeRetrievedSuccessfully() {
        // Given
        var mockCollection = createMockCollection();
        when(connectionManager.getCollection()).thenReturn(mockCollection);
        JsonObject expectedJson = JsonObject.create().put("field", "value");

        // Mocking GetResult to return expected JSON data
        GetResult mockGetResult = mock(GetResult.class);
        when(mockCollection.get(TEST_KEY)).thenReturn(mockGetResult);
        when(mockGetResult.contentAs(JsonObject.class)).thenReturn(expectedJson);

        // When
        JsonObject actualJson = couchbaseService.retrieve(TEST_KEY, couchbaseMetrics);

        // Then
        assertEquals(expectedJson, actualJson);
        verify(mockCollection, times(1)).get(TEST_KEY);
    }

    @Test
    @DisplayName("Test retrieve functionality throws CouchbaseException when document is not found")
    public void givenDocumentNotFound_whenRetrieve_thenShouldThrowCouchbaseException() {
        // Given
        var mockCollection = createMockCollection();
        when(connectionManager.getCollection()).thenReturn(mockCollection);
        when(mockCollection.get(NON_EXISTENT_KEY)).thenThrow(new CouchbaseException("Document not found"));

        // When
        CouchbaseException thrownException = assertThrows(CouchbaseException.class, () -> {
            couchbaseService.retrieve(NON_EXISTENT_KEY, couchbaseMetrics);
        });

        // Then
        assertEquals("Document not found", thrownException.getMessage());
    }

    @Test
    @DisplayName("Test retrieve functionality throws RuntimeException on unexpected errors")
    public void givenUnexpectedException_whenRetrieve_thenShouldThrowRuntimeException() {
        // Given
        var mockCollection = createMockCollection();
        when(connectionManager.getCollection()).thenReturn(mockCollection);
        when(mockCollection.get(TEST_KEY)).thenThrow(new RuntimeException(UNEXPECTED_ERROR_MESSAGE));

        // When
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            couchbaseService.retrieve(TEST_KEY, couchbaseMetrics);
        });

        // Then
        assertEquals("Unexpected error retrieving document with key: " + TEST_KEY, thrownException.getMessage());
    }

    @Test
    @DisplayName("Test retrieveJsonThreeTimes successfully retrieves JSON document")
    public void givenValidKey_whenRetrieveJsonThreeTimes_thenShouldRetrieveSuccessfully() {
        // Given
        var mockCollection = createMockCollection();
        when(connectionManager.getCollection()).thenReturn(mockCollection);
        JsonObject expectedJson = JsonObject.create().put("field", "value");

        // Mocking the collection's behavior to return expected JSON data
        GetResult mockGetResult = mock(GetResult.class);
        when(mockCollection.get(TEST_KEY)).thenReturn(mockGetResult);
        when(mockGetResult.contentAs(JsonObject.class)).thenReturn(expectedJson);

        // When
        couchbaseService.retrieveJsonThreeTimes(TEST_KEY, couchbaseMetrics);

        // Then
        verify(mockCollection, times(3)).get(TEST_KEY);
    }

    @Test
    @DisplayName("Test retrieveJsonThreeTimes throws CouchbaseException on first attempt")
    public void givenCouchbaseExceptionOnFirstAttempt_whenRetrieveJsonThreeTimes_thenShouldThrowCouchbaseException() {
        // Given
        var mockCollection = createMockCollection();
        when(connectionManager.getCollection()).thenReturn(mockCollection);
        when(mockCollection.get(TEST_KEY)).thenThrow(new CouchbaseException(CONNECTION_FAILED_MESSAGE));

        // When & Then
        CouchbaseException thrownException = assertThrows(CouchbaseException.class, () -> {
            couchbaseService.retrieveJsonThreeTimes(TEST_KEY, couchbaseMetrics);
        });

        assertEquals(CONNECTION_FAILED_MESSAGE, thrownException.getMessage());
        verify(mockCollection, times(1)).get(TEST_KEY); // Verify that it only tried once
    }

    @Test
    @DisplayName("Test retrieveJsonThreeTimes throws RuntimeException on unexpected error")
    public void givenUnexpectedExceptionOnFirstAttempt_whenRetrieveJsonThreeTimes_thenShouldThrowRuntimeException() {
        // Given
        var mockCollection = createMockCollection();
        when(connectionManager.getCollection()).thenReturn(mockCollection);
        when(mockCollection.get(TEST_KEY)).thenThrow(new RuntimeException(UNEXPECTED_ERROR_MESSAGE));

        // When & Then
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            couchbaseService.retrieveJsonThreeTimes(TEST_KEY, couchbaseMetrics);
        });

        assertEquals("Unexpected error retrieving document with key: " + TEST_KEY, thrownException.getMessage());
        verify(mockCollection, times(1)).get(TEST_KEY); // Verify that it only tried once
    }
}
