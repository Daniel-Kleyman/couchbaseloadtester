package kleyman.testrunner;

import kleyman.config.CouchbaseConnectionManager;
import kleyman.loadtest.CouchbaseLoadTestScenarioProvider;
import kleyman.loadtest.CouchbaseLoadTestExecutor;
import kleyman.service.CouchbaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class runs load tests on Couchbase by executing various connection
 * pool and thread pool scenarios while managing Couchbase connections.
 */

public class CouchbaseTestRunner implements TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseTestRunner.class);

    @Override
    public void runTests() {
        logger.info("Starting Couchbase Load Tests");
        // runThreadPoolTest();
        runConnectionPoolTest();
        logger.info("All load tests completed.");
    }

    private void runConnectionPoolTest() {

        for (Integer connectionPoolSize : CouchbaseLoadTestScenarioProvider.connectionPoolSize) {
            try (CouchbaseConnectionManager connectionManager = createConnectionManager(connectionPoolSize)) {
                if (initializeCouchbaseBucket(connectionManager)) {
                    CouchbaseService couchbaseService = new CouchbaseService(connectionManager);
                    CouchbaseLoadTestScenarioProvider scenarioProvider = new CouchbaseLoadTestScenarioProvider(couchbaseService);
                    CouchbaseLoadTestExecutor connectionPoolTestExecutor = scenarioProvider.getExecutorForConnectionPoolTest();
                    executeSingleLoadTest(connectionPoolTestExecutor);
                }
            } catch (Exception e) {
                logger.error("Error initializing Couchbase connection manager", e);
            }
        }
        logger.info("All connection pool tests completed.");
    }

    private void runThreadPoolTest() {
        try (CouchbaseConnectionManager connectionManager = createConnectionManager(0)) {
            if (initializeCouchbaseBucket(connectionManager)) {
                CouchbaseService couchbaseService = new CouchbaseService(connectionManager);
                executeLoadTests(new CouchbaseLoadTestScenarioProvider(couchbaseService).createThreadPoolScenarios());
            }
        } catch (Exception e) {
            logger.error("Error initializing Couchbase connection manager", e);
        }
        logger.info("All thread pool tests completed.");
    }

    private void executeLoadTests(Iterable<CouchbaseLoadTestExecutor> scenarios) {
        for (CouchbaseLoadTestExecutor scenario : scenarios) {
            executeSingleLoadTest(scenario);
        }
    }

    private void executeSingleLoadTest(CouchbaseLoadTestExecutor scenario) {
        logger.info("Running scenario with {} threads and uniqueKeys: {}", scenario.getThreadCount(), scenario.isUseUniqueKeys());
        scenario.executeLoadTest();
        logger.info("Scenario completed successfully.");
    }

    private CouchbaseConnectionManager createConnectionManager(int connectionPoolSize) {
        try {
            return new CouchbaseConnectionManager(connectionPoolSize);
        } catch (Exception e) {
            logger.error("Error initializing Couchbase connection manager with pool size: {}", connectionPoolSize, e);
            throw new RuntimeException("Failed to create CouchbaseConnectionManager", e);
        }
    }

    /**
     * Ensures the Couchbase bucket is opened by performing an initial operation.
     * This should be called before starting load test threads to avoid the lazy bucket opening during the test.
     */
    private boolean initializeCouchbaseBucket(CouchbaseConnectionManager connectionManager) {
        logger.info("Initializing Couchbase bucket...");
        try {
            connectionManager.initializeBucket();
            return true;
        } catch (Exception e) {
            logger.error("Failed to initialize Couchbase bucket. Aborting load test.", e);
            return false;
        }
    }
}