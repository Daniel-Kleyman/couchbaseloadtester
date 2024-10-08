package kleyman.loadtest;

import kleyman.service.CouchbaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CouchbaseLoadTestScenarioProviderTest {

    @Mock
    private CouchbaseService couchbaseService;

    private CouchbaseLoadTestScenarioProvider scenarioProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scenarioProvider = new CouchbaseLoadTestScenarioProvider(couchbaseService);
    }

    @Test
    @DisplayName("Test createThreadPoolScenarios returns 12 scenarios")
    public void givenCouchbaseService_whenCreateThreadPoolScenarios_thenReturns12Scenarios() {
        // Given

        // When
        List<CouchbaseLoadTestExecutor> scenarios = scenarioProvider.createThreadPoolScenarios();

        // Then
        assertNotNull(scenarios, "Scenarios list should not be null.");
        assertEquals(12, scenarios.size(), "Should create 12 load test scenarios.");

        for (CouchbaseLoadTestExecutor executor : scenarios) {
            assertNotNull(executor, "Each scenario should not be null.");
        }
    }

}
