package kleyman.metrics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MetricManagerTest {

    @Test
    @DisplayName("Test metricsMap is not null")
    void GivenMetricsMap_WhenCheckingNotNull_ThenMapIsNotNull() {
        // Given
        // (No setup required)

        // When
        Map<String, CouchbaseMetrics> metricsMap = MetricManager.metricsMap;

        // Then
        assertNotNull(metricsMap);
    }
}