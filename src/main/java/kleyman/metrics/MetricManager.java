package kleyman.metrics;

import java.util.HashMap;
import java.util.Map;

public class MetricManager {
    public static Map<String, CouchbaseMetrics> metricsMap = new HashMap<>();
    ;

    private MetricManager() {
    }
}
