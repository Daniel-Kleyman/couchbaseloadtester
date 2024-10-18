package kleyman.report;

import kleyman.metrics.CouchbaseMetrics;
import kleyman.metrics.MetricManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The {@code MetricsTableGenerator} class is responsible for generating
 * a metrics table for different scenarios based on the metrics collected
 * by the {@code MetricManager}. It provides methods to generate table data
 * specifically for thread pool and connection pool scenarios.
 *
 * <p>This class retrieves metrics from a {@code Map<String, CouchbaseMetrics>}
 * and organizes the data into a 2D array format suitable for reporting.</p>
 *
 * <p>Each table includes headers such as Scenario ID, Total Successful Operations,
 * Error Rate, Transactions Per Second, and various latency metrics.</p>
 */
public class MetricsTableDataGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MetricsTableDataGenerator.class);
    private static final int THREAD_POOL_START_INDEX = 1;
    private static final int THREAD_POOL_END_INDEX = 3;
    private static final String[] HEADERS = {
            "Scenario ID",
            "Total Successful Operations",
            "Total Error Rate (%)",
            "Transactions Per Second (TPS)",
            "Average PUT Latency (ms)",
            "Average GET Latency (ms)",
            "Overall Average Response Time (ms)"
    };

    private final Map<String, CouchbaseMetrics> metricsMap;

    public MetricsTableDataGenerator() {
        this.metricsMap = MetricManager.metricsMap;
    }

    public String[][] generateThreadPoolMetricsTableData() {
        return generateMetricsTableDataForScenarioType();
    }

    private String[][] generateMetricsTableDataForScenarioType() {
        logger.debug("Generating metrics table data for scenarios {} to {}", MetricsTableDataGenerator.THREAD_POOL_START_INDEX, MetricsTableDataGenerator.THREAD_POOL_END_INDEX);
        String[][] tableData = new String[MetricsTableDataGenerator.THREAD_POOL_END_INDEX - MetricsTableDataGenerator.THREAD_POOL_START_INDEX + 2][HEADERS.length];
        System.arraycopy(HEADERS, 0, tableData[0], 0, HEADERS.length);

        int rowIndex = 1;
        for (int i = MetricsTableDataGenerator.THREAD_POOL_START_INDEX; i <= MetricsTableDataGenerator.THREAD_POOL_END_INDEX; i++) {
            String scenarioId = "Scenario " + i;
            CouchbaseMetrics metrics = metricsMap.get(scenarioId);
            if (metrics != null) {
                tableData[rowIndex][0] = creatScenarioId(scenarioId, metrics);
                tableData[rowIndex][1] = String.valueOf(metrics.getTotalSuccessfulOperations());
                tableData[rowIndex][2] = String.format("%.2f", metrics.getTotalErrorRate());
                tableData[rowIndex][3] = String.format("%.2f", metrics.getTransactionsPerSecond());
                tableData[rowIndex][4] = String.format("%.2f", metrics.getAveragePutLatency());
                tableData[rowIndex][5] = String.format("%.2f", metrics.getAverageGetLatency());
                tableData[rowIndex][6] = String.format("%.2f", metrics.getOverallAverageResponseTime());
                rowIndex++;
            } else {
                logger.warn("No metrics found for {}", scenarioId);
            }
        }
        logger.debug("Metrics table data generation completed with {} rows.", rowIndex);
        return tableData;
    }

    private String creatScenarioId(String scenarioId, CouchbaseMetrics metrics) {

        String scenarioIdRes = scenarioId;

        scenarioIdRes = scenarioIdRes + ": " + "threads=" + metrics.getThreadSize() + "," + getJsonSize(metrics)
                + "," + getKey(metrics);

        return scenarioIdRes;
    }

    private String getJsonSize(CouchbaseMetrics metrics) {
        if (metrics.getJsonSize().contains("big")) {
            return "25kb";
        } else return "1kb";
    }

    private String getKey(CouchbaseMetrics metrics) {
        if (metrics.isUniqueKeys()) {
            return "unique keys";
        } else return "shared key";
    }
}
