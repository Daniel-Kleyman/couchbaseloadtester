package kleyman.testrunner;

import kleyman.config.CouchbaseConnectionManager;
import kleyman.loadtest.CouchbaseLoadTestScenarioFactory;
import kleyman.loadtest.CouchbaseLoadTestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CouchbaseTestRunner implements TestRunner {
    private static final Logger logger = LoggerFactory.getLogger(CouchbaseTestRunner.class);

    /**
     * Runs all load test scenarios for Couchbase, establishes and closes connection
     * with Couchbase after tests are finished.
     */
    @Override
    public void runTests() {
        logger.info("Starting Couchbase Load Tests");

        try (CouchbaseConnectionManager connectionManager = new CouchbaseConnectionManager()) {
            CouchbaseLoadTestScenarioFactory scenarioFactory = new CouchbaseLoadTestScenarioFactory(connectionManager);
            for (CouchbaseLoadTestExecutor scenario : scenarioFactory.createScenarios()) {
                try {
                    logger.info("Running scenario with {} threads and uniqueKeys={}",
                            scenario.getThreadCount(),
                            scenario.isUseUniqueKeys());
                    scenario.executeLoadTest();
                    logger.info("Scenario completed successfully.");
                } catch (Exception e) {
                    logger.error("Error running scenario", e);
                }
            }
        } catch (Exception e) {
            logger.error("Error initializing Couchbase connection manager", e);
        }

        logger.info("All load tests completed.");
    }
}