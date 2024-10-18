package kleyman.testrunner;

import io.micrometer.core.instrument.MeterRegistry;
import kleyman.config.CouchbaseConnectionManager;
import kleyman.loadtest.CouchbaseLoadTestScenarioProvider;
import kleyman.loadtest.CouchbaseLoadTestExecutor;
import kleyman.service.CouchbaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class runs load tests on Couchbase by executing various connection
 * pool and thread pool scenarios while managing Couchbase connections.
 */

public class CouchbaseTestRunner implements TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseTestRunner.class);
    int numberOfTestRun = 0;
    private final List<List<CompletableFuture<Void>>> futureLists = new ArrayList<>();

    @Override
    public void runTests() {
        logger.info("Starting Couchbase Load Tests");
        runThreadPoolTest();
        logger.info("All {} load tests completed.", numberOfTestRun);
    }

    private void runThreadPoolTest() {
        try (CouchbaseConnectionManager connectionManager = createConnectionManager(0)) {
            if (initializeCouchbaseBucket(connectionManager)) {
                CouchbaseService couchbaseService = new CouchbaseService(connectionManager);
                executeLoadTests(new CouchbaseLoadTestScenarioProvider(couchbaseService).createThreadPoolScenarios());
                waitForAllTasksToComplete();
            }
        } catch (Exception e) {
            logger.error("Error initializing Couchbase connection manager", e);
        }
        logger.info("All thread pool tests completed.");
    }

    private void waitForAllTasksToComplete() {
        for (List<CompletableFuture<Void>> futureList : futureLists) {
            try {
                CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
                allFutures.join();
                logger.info("All asynchronous tasks have completed.");
            } catch (Exception e) {
                logger.error("Error while waiting for tasks to complete", e);
            }
        }

    }

    private void executeLoadTests(Iterable<CouchbaseLoadTestExecutor> scenarios) {
        for (CouchbaseLoadTestExecutor scenario : scenarios) {
            executeSingleLoadTest(scenario);
            futureLists.add(scenario.getFutureList());
        }
    }

    private void executeSingleLoadTest(CouchbaseLoadTestExecutor scenario) {
        logger.info("Running scenario with {} threads and uniqueKeys: {}", scenario.getThreadCount(), scenario.isUseUniqueKeys());
        scenario.executeLoadTest();
        logger.info("Scenario completed successfully.");
        numberOfTestRun++;
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