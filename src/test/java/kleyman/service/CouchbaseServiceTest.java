package kleyman.service;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import kleyman.config.CouchbaseConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class CouchbaseServiceTest {
    private CouchbaseConnectionManager connectionManager;
    private CouchbaseService couchbaseService;
    private JsonObject jsonData;

    @BeforeEach
    void setUp() {
        connectionManager = Mockito.mock(CouchbaseConnectionManager.class);
        var mockCollection = Mockito.mock(com.couchbase.client.java.Collection.class);
        when(connectionManager.getCollection()).thenReturn(mockCollection);
        couchbaseService = new CouchbaseService(connectionManager);
        jsonData = JsonObject.create().put("field", "value");
    }

    @Test
    @DisplayName("Test document insertion with valid JSON data")
    public void givenValidJson_whenInsertDocument_thenDocumentShouldBeInsertedSuccessfully() {
        // Given
        String key = "testKey";

        // When
        couchbaseService.upload(key, jsonData);

        // Then
        verify(connectionManager.getCollection(), times(1)).upsert(key, jsonData);
    }

    @Test
    @DisplayName("Test upload functionality throws CouchbaseException on failure")
    public void givenCouchbaseException_whenUpload_thenShouldThrowCouchbaseException() {
        // Given
        var mockCollection = connectionManager.getCollection();
        when(mockCollection.upsert(anyString(), any())).thenThrow(new CouchbaseException("Connection failed"));

        // When
        CouchbaseException thrownException = assertThrows(CouchbaseException.class, () -> {
            couchbaseService.upload("testKey", jsonData);
        });

        // Then
        assertEquals("Connection failed", thrownException.getMessage());
    }

    @Test
    @DisplayName("Test upload functionality throws RuntimeException on unexpected errors")
    public void givenUnexpectedException_whenUpload_thenShouldThrowRuntimeException() {
        // Given
        var mockCollection = connectionManager.getCollection();
        when(mockCollection.upsert(anyString(), any())).thenThrow(new RuntimeException("Unexpected error"));

        // Ensure that the CouchbaseService uses the mocked collection
        when(connectionManager.getCollection()).thenReturn(mockCollection);

        // When
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            couchbaseService.upload("testKey", jsonData);
        });

        // Then
        assertEquals("Unexpected error inserting document with key: testKey", thrownException.getMessage());
    }

    @Test
    void givenValidKey_whenRetrieveDocument_thenDocumentShouldBeRetrievedSuccessfully() {
        // Given
        String key = "testKey";
        JsonObject expectedJson = JsonObject.create().put("field", "value");
        var mockCollection = connectionManager.getCollection();

        // Mocking GetResult to return expected JSON data
        GetResult mockGetResult = mock(GetResult.class);
        when(mockCollection.get(key)).thenReturn(mockGetResult);
        when(mockGetResult.contentAs(JsonObject.class)).thenReturn(expectedJson);

        // When
        JsonObject actualJson = couchbaseService.retrieve(key);

        // Then
        assertEquals(expectedJson, actualJson);
        verify(mockCollection, times(1)).get(key);
    }

    @Test
    @DisplayName("Test retrieve functionality throws CouchbaseException when document is not found")
    public void givenDocumentNotFound_whenRetrieve_thenShouldThrowCouchbaseException() {
        // Given
        String key = "nonExistentKey";
        var mockCollection = connectionManager.getCollection();

        // Mocking the collection's behavior to throw a CouchbaseException when the document is not found
        when(mockCollection.get(key)).thenThrow(new CouchbaseException("Document not found"));

        // When
        CouchbaseException thrownException = assertThrows(CouchbaseException.class, () -> {
            couchbaseService.retrieve(key);
        });

        // Then
        assertEquals("Document not found", thrownException.getMessage());
    }

    @Test
    @DisplayName("Test retrieve functionality throws RuntimeException on unexpected errors")
    public void givenUnexpectedException_whenRetrieve_thenShouldThrowRuntimeException() {
        // Given
        String key = "testKey";
        var mockCollection = connectionManager.getCollection();

        // Mocking the collection's behavior to throw an unexpected RuntimeException
        when(mockCollection.get(key)).thenThrow(new RuntimeException("Unexpected error"));

        // When
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            couchbaseService.retrieve(key);
        });

        // Then
        assertEquals("Unexpected error retrieving document with key: testKey", thrownException.getMessage());
    }

    @Test
    @DisplayName("Test retrieveJsonThreeTimes successfully retrieves JSON document")
    public void givenValidKey_whenRetrieveJsonThreeTimes_thenShouldRetrieveSuccessfully() {
        // Given
        String key = "testKey";
        JsonObject expectedJson = JsonObject.create().put("field", "value");
        var mockCollection = connectionManager.getCollection();

        // Mocking the collection's behavior to return expected JSON data
        GetResult mockGetResult = mock(GetResult.class);
        when(mockCollection.get(key)).thenReturn(mockGetResult);
        when(mockGetResult.contentAs(JsonObject.class)).thenReturn(expectedJson);

        // When
        couchbaseService.retrieveJsonThreeTimes(key);

        // Then
        verify(mockCollection, times(3)).get(key);
    }

    @Test
    @DisplayName("Test retrieveJsonThreeTimes throws CouchbaseException on first attempt")
    public void givenCouchbaseExceptionOnFirstAttempt_whenRetrieveJsonThreeTimes_thenShouldThrowCouchbaseException() {
        // Given
        String key = "testKey";
        var mockCollection = connectionManager.getCollection();

        // Mocking the collection's behavior to throw CouchbaseException on first attempt
        when(mockCollection.get(key)).thenThrow(new CouchbaseException("Connection failed"));

        // When & Then
        CouchbaseException thrownException = assertThrows(CouchbaseException.class, () -> {
            couchbaseService.retrieveJsonThreeTimes(key);
        });

        assertEquals("Connection failed", thrownException.getMessage());
        verify(mockCollection, times(1)).get(key); // Verify that it only tried once
    }

    @Test
    @DisplayName("Test retrieveJsonThreeTimes throws RuntimeException on unexpected error")
    public void givenUnexpectedExceptionOnFirstAttempt_whenRetrieveJsonThreeTimes_thenShouldThrowRuntimeException() {
        // Given
        String key = "testKey";
        var mockCollection = connectionManager.getCollection();

        // Mocking the collection's behavior to throw RuntimeException on first attempt
        when(mockCollection.get(key)).thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            couchbaseService.retrieveJsonThreeTimes(key);
        });

        assertEquals("Unexpected error retrieving document with key: testKey", thrownException.getMessage());
        verify(mockCollection, times(1)).get(key); // Verify that it only tried once
    }
}
