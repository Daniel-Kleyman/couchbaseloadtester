package kleyman.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;

import java.util.concurrent.TimeUnit;

/**
 * Metrics collector for Couchbase operations.
 * This class manages metrics for PUT and GET operations using Micrometer,
 * including counters for success and failure rates, and timers for operation latencies.
 *
 * <p>It provides methods to increment counters and record latencies for
 * performance monitoring and analysis.</p>
 */
public class CouchbaseMetrics {

    private final Counter putSuccessCounter;
    private final Counter putFailureCounter;
    private final Counter getSuccessCounter;
    private final Counter getFailureCounter;
    private final Timer putTimer;
    private final Timer getTimer;

    public CouchbaseMetrics(MeterRegistry meterRegistry, String scenarioId) {
        // Initialize counters
        putSuccessCounter = Counter.builder("couchbase.put.success")
                .description("Count of successful PUT operations")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        putFailureCounter = Counter.builder("couchbase.put.failure")
                .description("Count of failed PUT operations")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        getSuccessCounter = Counter.builder("couchbase.get.success")
                .description("Count of successful GET operations")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        getFailureCounter = Counter.builder("couchbase.get.failure")
                .description("Count of failed GET operations")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        putTimer = Timer.builder("couchbase.put.response_time")
                .description("Latency of PUT operations")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        getTimer = Timer.builder("couchbase.get.response_time")
                .description("Latency of GET operations")
                .tag("scenario", scenarioId)
                .register(meterRegistry);
    }

    public void incrementPutSuccess() {
        putSuccessCounter.increment();
    }

    public void incrementPutFailure() {
        putFailureCounter.increment();
    }

    public void incrementGetSuccess() {
        getSuccessCounter.increment();
    }

    public void incrementGetFailure() {
        getFailureCounter.increment();
    }

    public void recordPutLatency(long duration, TimeUnit unit) {
        putTimer.record(duration, unit);
    }

    public void recordGetLatency(long duration, TimeUnit unit) {
        getTimer.record(duration, unit);
    }
}
