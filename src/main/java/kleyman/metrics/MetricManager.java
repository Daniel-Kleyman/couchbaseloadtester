package kleyman.metrics;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages metrics for Couchbase operations by storing them in a shared map.
 * Provides a centralized location for accessing and managing CouchbaseMetrics instances.
 */
public class MetricManager {
    public static Map<String, CouchbaseMetrics> metricsMap = new HashMap<>();
}
