//package kleyman.it;
//
//package kleyman.service;
//
//import com.couchbase.client.core.error.CouchbaseException;
//import com.couchbase.client.java.Bucket;
//import com.couchbase.client.java.Cluster;
//import com.couchbase.client.java.json.JsonObject;
//import kleyman.config.CouchbaseConnectionManager;
//import kleyman.service.CouchbaseService;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.testcontainers.containers.CouchbaseContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@Testcontainers
//class CouchbaseServiceIntegrationTest {
//
//    @Container
//    private static final CouchbaseContainer couchbaseContainer = new CouchbaseContainer("couchbase:latest")
//            .withBucket("testBucket");
//
//    private static CouchbaseService couchbaseService;
//
//    @BeforeAll
//    static void setUp() {
//        Cluster cluster = Cluster.connect(couchbaseContainer.getHost(),
//                couchbaseContainer.getUsername(),
//                couchbaseContainer.getPassword());
//        Bucket bucket = cluster.bucket("testBucket");
//        CouchbaseConnectionManager connectionManager = new CouchbaseConnectionManager(0);
//        couchbaseService = new CouchbaseService(connectionManager);
//    }
//
//    @Test
//    void givenValidKeyAndJsonData_whenUploadCalled_thenDocumentInsertedSuccessfully() {
//        String key = "testKey";
//        JsonObject jsonData = JsonObject.create().put("field", "value");
//
//        couchbaseService.upload(key, jsonData);
//
//        JsonObject retrievedData = couchbaseService.retrieve(key);
//        assertEquals(jsonData, retrievedData);
//    }
//
//    @Test
//    void givenValidKey_whenRetrieveCalled_thenDocumentRetrievedSuccessfully() {
//        String key = "testKey";
//        JsonObject jsonData = JsonObject.create().put("field", "value");
//        couchbaseService.upload(key, jsonData);
//
//        JsonObject result = couchbaseService.retrieve(key);
//
//        assertEquals(jsonData, result);
//    }
//
//    @Test
//    void givenInvalidKey_whenRetrieveCalled_thenThrowCouchbaseException() {
//        String key = "invalidKey";
//
//        CouchbaseException thrown = assertThrows(CouchbaseException.class, () -> couchbaseService.retrieve(key));
//        assertEquals("Document not found for key: invalidKey", thrown.getMessage());
//    }
//}
