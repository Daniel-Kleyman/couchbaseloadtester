package kleyman;

import io.micrometer.core.instrument.*;
import kleyman.metrics.MetricsSetup;
import kleyman.testrunner.CouchbaseTestRunner;
import kleyman.metrics.MetricReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Manages the lifecycle of metrics reporting using {@link MetricReporter}
 * and runs the Couchbase tests using {@link CouchbaseTestRunner}.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        MetricReporter metricReporter = new MetricReporter();

        try {
            metricReporter.startReporting();
            logger.info("Metrics reporting started.");
            CouchbaseTestRunner testRunner = new CouchbaseTestRunner();
            testRunner.runTests();
           // MeterRegistry meterRegistry = MetricsSetup.getRegistry();
           // logMetrics(meterRegistry);
        } catch (Exception e) {
            logger.error("Error during test execution: {}", e.getMessage(), e);
        } finally {
          //  metricReporter.stopReporting();
            logger.info("Metrics reporting stopped.");
        }
    }
//    public static void logMetrics(MeterRegistry meterRegistry) {
//        // Iterate over all meters in the registry
//        for (Meter meter : meterRegistry.getMeters()) {
//            // Log meter name
//            logger.info("Meter Name: {}", meter.getId().getName());
//
//            // Log tags associated with the meter
//            List<Tag> tags = meter.getId().getTags();
//            if (!tags.isEmpty()) {
//                logger.info("Tags: {}", tags);
//            }
//
//            // Extract specific metrics based on meter type
//            if (meter instanceof Counter) {
//                Counter counter = (Counter) meter;
//                logger.info("Counter value: {}", counter.count());
//            } else if (meter instanceof Timer) {
//                Timer timer = (Timer) meter;
//                logger.info("Timer count: {}", timer.count());
//                logger.info("Total time (ms): {}", timer.totalTime(TimeUnit.MILLISECONDS));
//            }
//            // Add more cases for different meter types as needed
//        }
//    }
}
