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
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseLoadTestScenarioProvider.class); // Logger instance
    private final CouchbaseService couchbaseService;
    private final String jsonBigPath = EnvironmentVariableUtils.getEnv("JSON_BIG_PATH");
    private final String jsonSmallPath = EnvironmentVariableUtils.getEnv("JSON_SMALL_PATH");
    public static final List<Integer> connectionPoolSize = List.of(5, 10, 15);

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
                new CouchbaseLoadTestExecutor(5, jsonBigPath, true, couchbaseService, "scenario1"),
                new CouchbaseLoadTestExecutor(5, jsonBigPath, false, couchbaseService, "scenario2"),
                new CouchbaseLoadTestExecutor(10, jsonBigPath, true, couchbaseService, "scenario3"),
                new CouchbaseLoadTestExecutor(10, jsonBigPath, false, couchbaseService, "scenario4"),
                new CouchbaseLoadTestExecutor(15, jsonBigPath, true, couchbaseService, "scenario5"),
                new CouchbaseLoadTestExecutor(15, jsonBigPath, false, couchbaseService, "scenario6"),
                new CouchbaseLoadTestExecutor(5, jsonSmallPath, true, couchbaseService, "scenario7"),
                new CouchbaseLoadTestExecutor(5, jsonSmallPath, false, couchbaseService, "scenario8"),
                new CouchbaseLoadTestExecutor(10, jsonSmallPath, true, couchbaseService, "scenario9"),
                new CouchbaseLoadTestExecutor(10, jsonSmallPath, false, couchbaseService, "scenario10"),
                new CouchbaseLoadTestExecutor(15, jsonSmallPath, true, couchbaseService, "scenario11"),
                new CouchbaseLoadTestExecutor(15, jsonSmallPath, false, couchbaseService, "scenario12")
        );
        logger.info("Created {} thread pool load test scenarios.", scenarios.size());
        return scenarios;
    }

    public List<CouchbaseLoadTestExecutor> createConnectionPoolScenarios() {
        logger.info("Creating Couchbase load test connection pool scenarios.");
        List<CouchbaseLoadTestExecutor> scenarios = List.of(
                new CouchbaseLoadTestExecutor(10, jsonBigPath, true, couchbaseService, "scenario13"),
                new CouchbaseLoadTestExecutor(10, jsonBigPath, true, couchbaseService, "scenario14"),
                new CouchbaseLoadTestExecutor(10, jsonBigPath, true, couchbaseService, "scenario15"));
        logger.info("Created {} connection pool load test scenarios.", scenarios.size());
        return scenarios;
    }

}
