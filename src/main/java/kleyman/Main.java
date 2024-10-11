package kleyman;

import kleyman.metrics.MetricsSetup;
import kleyman.testrunner.CouchbaseTestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the lifecycle of metrics reporting using {@link MetricsSetup}
 * and runs the Couchbase tests using {@link CouchbaseTestRunner}.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            MetricsSetup.setupMetrics();
            logger.info("Metrics reporting started.");
            CouchbaseTestRunner testRunner = new CouchbaseTestRunner();
            testRunner.runTests();
        } catch (Exception e) {
            logger.error("Error during test execution: {}", e.getMessage(), e);
        } finally {
           // MetricsSetup.stopMetricsServer();
            logger.info("Metrics reporting stopped.");
        }
    }
}
