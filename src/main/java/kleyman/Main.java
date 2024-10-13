package kleyman;

import kleyman.metrics.MetricsSetup;
import kleyman.report.PPTXReportGenerator;
import kleyman.testrunner.CouchbaseTestRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Manages the lifecycle of metrics reporting using the MetricsSetup class,
 * runs the Couchbase tests using the CouchbaseTestRunner class,
 * and generates a report in PowerPoint format.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        try {
            MetricsSetup.setupMetrics();
            logger.info("Metrics reporting started.");
            CouchbaseTestRunner testRunner = new CouchbaseTestRunner();
            testRunner.runTests();
            PPTXReportGenerator report = new PPTXReportGenerator();
            report.createReport();
        } catch (Exception e) {
            logger.error("Error during test execution: {}", e.getMessage(), e);
        } finally {
            MetricsSetup.stopMetricsServer();
            logger.info("Metrics reporting stopped.");
        }
    }
}
