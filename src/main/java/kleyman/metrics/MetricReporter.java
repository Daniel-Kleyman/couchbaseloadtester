package kleyman.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MetricReporter is responsible for reporting the collected metrics to Prometheus
 * or any other monitoring service. This class will provide methods to start, stop,
 * or retrieve the current metrics.
 */
public class MetricReporter {

    private static final Logger logger = LoggerFactory.getLogger(MetricReporter.class);

    public void startReporting() {
        logger.info("Starting to report metrics to Prometheus.");
        MetricsSetup.setupMetrics();
    }

//    public void stopReporting() {
//        logger.info("Stopping metrics reporting.");
//        MetricsSetup.stopMetricsServer();
//    }

}
