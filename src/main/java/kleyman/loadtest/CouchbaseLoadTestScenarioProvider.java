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
    private final String jsonSmallPath = EnvironmentVariableUtils.getEnv("JSON_SMALL_PATH");
    private static final int[] THREAD_COUNTS = {1, 2, 3};
    private static final String SCENARIO_PREFIX = "Scenario ";

    public CouchbaseLoadTestScenarioProvider(CouchbaseService couchbaseService) {
        this.couchbaseService = couchbaseService;
    }

    /**
     * Creates load test scenarios for Couchbase.
     * Defines scenarios with 1, 2, or 3 threads, using small JSON data,
     * and using unique keys for each operation.
     *
     * @return a list of CouchbaseLoadTestExecutor scenarios
     */
    public List<CouchbaseLoadTestExecutor> createThreadPoolScenarios() {
        logger.info("Creating Couchbase load test thread pool scenarios.");

        List<CouchbaseLoadTestExecutor> scenarios = List.of(
                createExecutor(THREAD_COUNTS[0], jsonSmallPath, true, 1),
                createExecutor(THREAD_COUNTS[1], jsonSmallPath, true, 2),
                createExecutor(THREAD_COUNTS[2], jsonSmallPath, true, 3)
        );
        logger.info("Created {} thread pool load test scenarios.", scenarios.size());
        return scenarios;
    }

    private CouchbaseLoadTestExecutor createExecutor(int threadCount, String jsonPath, boolean uniqueKeys, int scenarioNumber) {
        return new CouchbaseLoadTestExecutor(threadCount, jsonPath, uniqueKeys, couchbaseService, SCENARIO_PREFIX + scenarioNumber);
    }
}
