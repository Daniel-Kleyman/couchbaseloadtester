package kleyman.loadtest;

import java.util.List;

import kleyman.service.CouchbaseService;
import kleyman.util.EnvironmentVariableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kleyman.config.CouchbaseConnectionManager;

/**
 * Factory class for creating load testing scenarios for Couchbase.
 * This class defines various test scenarios for benchmarking Couchbase's key-value functionality.
 */
public class CouchbaseLoadTestScenarioFactory {
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseLoadTestScenarioFactory.class); // Logger instance
    private final CouchbaseService couchbaseService;
    private final String jsonBigPath = EnvironmentVariableUtils.getEnv("JSON_BIG_PATH");
    private final String jsonSmallPath = EnvironmentVariableUtils.getEnv("JSON_SMALL_PATH");

    public CouchbaseLoadTestScenarioFactory(CouchbaseConnectionManager connectionManager) {
        this.couchbaseService = new CouchbaseService(connectionManager);
    }

    /**
     * Creates load test scenarios for Couchbase.
     *
     * @return a list of CouchbaseLoadTestExecutor scenarios
     */
    public List<CouchbaseLoadTestExecutor> createScenarios() {
        logger.info("Creating Couchbase load test scenarios.");
        List<CouchbaseLoadTestExecutor> scenarios = List.of(
                new CouchbaseLoadTestExecutor(5, jsonBigPath, true, couchbaseService),
                new CouchbaseLoadTestExecutor(5, jsonBigPath, false, couchbaseService),
                new CouchbaseLoadTestExecutor(10, jsonBigPath, true, couchbaseService),
                new CouchbaseLoadTestExecutor(10, jsonBigPath, false, couchbaseService),
                new CouchbaseLoadTestExecutor(15, jsonBigPath, true, couchbaseService),
                new CouchbaseLoadTestExecutor(15, jsonBigPath, false, couchbaseService),
                new CouchbaseLoadTestExecutor(5, jsonSmallPath, true, couchbaseService),
                new CouchbaseLoadTestExecutor(5, jsonSmallPath, false, couchbaseService),
                new CouchbaseLoadTestExecutor(10, jsonSmallPath, true, couchbaseService),
                new CouchbaseLoadTestExecutor(10, jsonSmallPath, false, couchbaseService),
                new CouchbaseLoadTestExecutor(15, jsonSmallPath, true, couchbaseService),
                new CouchbaseLoadTestExecutor(15, jsonSmallPath, false, couchbaseService)
        );
        List<CouchbaseLoadTestExecutor> scenarios1 = List.of(
                new CouchbaseLoadTestExecutor(5, jsonBigPath, true, couchbaseService));
        logger.info("Created {} load test scenarios.", scenarios.size());
        return scenarios1;
    }
}
