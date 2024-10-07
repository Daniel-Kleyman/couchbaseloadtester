package kleyman.loadtest;

import java.util.List;
import kleyman.service.CouchbaseService;
import kleyman.util.EnvironmentVariableUtils;
import lombok.Getter;
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
    @Getter
    public final CouchbaseLoadTestExecutor executorForConnectionPoolTest;

    public CouchbaseLoadTestScenarioProvider(CouchbaseService couchbaseService) {
        this.couchbaseService = couchbaseService;
        executorForConnectionPoolTest = new CouchbaseLoadTestExecutor(10, jsonBigPath, true, couchbaseService);
    }

    /**
     * Creates load test scenarios for Couchbase.
     *
     * @return a list of CouchbaseLoadTestExecutor scenarios
     */
    public List<CouchbaseLoadTestExecutor> createThreadPoolScenarios() {
        logger.info("Creating Couchbase load test scenarios.");
        // Defines scenarios with 5, 10, or 15 threads, using either big or small JSON data, and using unique or shared keys for each operation.
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
        logger.info("Created {} load test scenarios.", scenarios.size());
        List<CouchbaseLoadTestExecutor> scenarios1 = List.of(
                new CouchbaseLoadTestExecutor(5, jsonBigPath, true, couchbaseService));
        return scenarios1;
    }


}
