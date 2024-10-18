package kleyman.loadtest;

import kleyman.service.CouchbaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CouchbaseLoadTestScenarioProviderTest {
    private CouchbaseLoadTestScenarioProvider scenarioProvider;

    @BeforeEach
    void setUp() {
        CouchbaseService couchbaseService = Mockito.mock(CouchbaseService.class);
        scenarioProvider = new CouchbaseLoadTestScenarioProvider(couchbaseService);
    }

    @Test
    @DisplayName("Test creation of load test scenarios")
    void givenCouchbaseLoadTestScenarioProvider_whenCreateThreadPoolScenariosCalled_thenReturnsListOfCouchbaseLoadTestExecutor() {
        // Given
        // A CouchbaseLoadTestScenarioProvider with a mocked CouchbaseService

        // When
        List<CouchbaseLoadTestExecutor> scenarios = scenarioProvider.createThreadPoolScenarios();

        // Then
        assertNotNull(scenarios, "Scenarios should not be null");
        assertEquals(12, scenarios.size(), "Expected 12 scenarios to be created");
    }
}
