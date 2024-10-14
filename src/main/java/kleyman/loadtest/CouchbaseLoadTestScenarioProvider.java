package kleyman.loadtest;

import java.util.List;

import kleyman.service.CouchbaseService;
import kleyman.util.EnvironmentVariableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating load testing scenarios for Couchbase.
 * This class defines various test scenarios for benchmarking Couchbase's key-value functionality.
 */
public class CouchbaseLoadTestScenarioProvider {
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseLoadTestScenarioProvider.class);
    private final CouchbaseService couchbaseService;
    private final String jsonBigPath = EnvironmentVariableUtils.getEnv("JSON_BIG_PATH");
    private final String jsonSmallPath = EnvironmentVariableUtils.getEnv("JSON_SMALL_PATH");
    private static final int[] THREAD_COUNTS = {5, 10, 15};
    public static final int[] CONNECTION_POOL_SIZE = {5, 10, 15};
    private static final String SCENARIO_PREFIX = "Scenario ";

    public CouchbaseLoadTestScenarioProvider(CouchbaseService couchbaseService) {
        this.couchbaseService = couchbaseService;
    }

    /**
     * Creates load test scenarios for Couchbase.
     * Defines scenarios with 5, 10, or 15 threads, using either big or small JSON data,
     * and using unique or shared keys for each operation.
     *
     * @return a list of CouchbaseLoadTestExecutor scenarios
     */
    public List<CouchbaseLoadTestExecutor> createThreadPoolScenarios() {
        logger.info("Creating Couchbase load test thread pool scenarios.");

        List<CouchbaseLoadTestExecutor> scenarios = List.of(
                createExecutor(THREAD_COUNTS[0], jsonBigPath, true, 1),
                createExecutor(THREAD_COUNTS[0], jsonBigPath, false, 2),
                createExecutor(THREAD_COUNTS[1], jsonBigPath, true, 3),
                createExecutor(THREAD_COUNTS[1], jsonBigPath, false, 4),
                createExecutor(THREAD_COUNTS[2], jsonBigPath, true, 5),
                createExecutor(THREAD_COUNTS[2], jsonBigPath, false, 6),
                createExecutor(THREAD_COUNTS[0], jsonSmallPath, true, 7),
                createExecutor(THREAD_COUNTS[0], jsonSmallPath, false, 8),
                createExecutor(THREAD_COUNTS[1], jsonSmallPath, true, 9),
                createExecutor(THREAD_COUNTS[1], jsonSmallPath, false, 10),
                createExecutor(THREAD_COUNTS[2], jsonSmallPath, true, 11),
                createExecutor(THREAD_COUNTS[2], jsonSmallPath, false, 12)
        );
        logger.info("Created {} thread pool load test scenarios.", scenarios.size());
        return scenarios;
    }

    public List<CouchbaseLoadTestExecutor> createConnectionPoolScenarios() {
        logger.info("Creating Couchbase load test connection pool scenarios.");
        List<CouchbaseLoadTestExecutor> scenarios = List.of(
                createExecutor(THREAD_COUNTS[1], jsonBigPath, true, 13),
                createExecutor(THREAD_COUNTS[1], jsonBigPath, true, 14),
                createExecutor(THREAD_COUNTS[1], jsonBigPath, true, 15));
        logger.info("Created {} connection pool load test scenarios.", scenarios.size());
        return scenarios;
    }

    private CouchbaseLoadTestExecutor createExecutor(int threadCount, String jsonPath, boolean uniqueKeys, int scenarioNumber) {
        return new CouchbaseLoadTestExecutor(threadCount, jsonPath, uniqueKeys, couchbaseService, SCENARIO_PREFIX + scenarioNumber);
    }
}
