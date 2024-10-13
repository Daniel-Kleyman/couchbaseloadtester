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
    private static final int THREAD_POOL_END_INDEX = 12;
    private static final int CONNECTION_POOL_START_INDEX = 13;
    private static final int CONNECTION_POOL_END_INDEX = 15;
    private static final String[] HEADERS = {
            "Scenario ID",
            "Total Successful Operations",
            "Total Error Rate (%)",
            "Transactions Per Second (TPS)",
            "Average PUT Latency (ms)",
            "Average GET Latency (ms)",
            "Overall Average Response Time (ms)",
            "Max PUT Latency (ms)",
            "Max GET Latency (ms)"
    };

    private final Map<String, CouchbaseMetrics> metricsMap;

    public MetricsTableDataGenerator() {
        this.metricsMap = MetricManager.metricsMap;
    }

    public String[][] generateThreadPoolMetricsTableData() {
        return generateMetricsTableDataForScenarioType(THREAD_POOL_START_INDEX, THREAD_POOL_END_INDEX);
    }

    public String[][] generateConnectionPoolMetricsTableData() {
        return generateMetricsTableDataForScenarioType(CONNECTION_POOL_START_INDEX, CONNECTION_POOL_END_INDEX);
    }

    private String[][] generateMetricsTableDataForScenarioType(int startIndex, int endIndex) {
        logger.debug("Generating metrics table data for scenarios {} to {}", startIndex, endIndex);
        String[][] tableData = new String[endIndex - startIndex + 2][HEADERS.length];
        System.arraycopy(HEADERS, 0, tableData[0], 0, HEADERS.length);

        int rowIndex = 1;
        for (int i = startIndex; i <= endIndex; i++) {
            String scenarioId = "Scenario " + i;
            CouchbaseMetrics metrics = metricsMap.get(scenarioId);
            if (metrics != null) {
                tableData[rowIndex][0] = scenarioId;
                tableData[rowIndex][1] = String.valueOf(metrics.getTotalSuccessfulOperations());
                tableData[rowIndex][2] = String.format("%.2f", metrics.getTotalErrorRate());
                tableData[rowIndex][3] = String.format("%.2f", metrics.getTransactionsPerSecond());
                tableData[rowIndex][4] = String.format("%.2f", metrics.getAveragePutLatency());
                tableData[rowIndex][5] = String.format("%.2f", metrics.getAverageGetLatency());
                tableData[rowIndex][6] = String.format("%.2f", metrics.getOverallAverageResponseTime());
                tableData[rowIndex][7] = String.format("%.2f", metrics.getMaxPutLatency());
                tableData[rowIndex][8] = String.format("%.2f", metrics.getMaxGetLatency());
                rowIndex++;
            } else {
                logger.warn("No metrics found for {}", scenarioId);
            }
        }
        logger.debug("Metrics table data generation completed with {} rows.", rowIndex);
        return tableData;
    }
}
