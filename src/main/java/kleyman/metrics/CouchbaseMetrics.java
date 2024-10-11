package kleyman.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(CouchbaseMetrics.class);
    private final Counter putSuccessCounter;
    private final Counter putFailureCounter;
    private final Counter getSuccessCounter;
    private final Counter getFailureCounter;
    private final Timer putTimer;
    private final Timer getTimer;

    public CouchbaseMetrics(MeterRegistry meterRegistry, String scenarioId) {
        logger.info("Starting collection of metrics");

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

        Gauge.builder("couchbase.put.average_latency", () -> {
                    double totalPutResponseTime = putTimer.totalTime(TimeUnit.MILLISECONDS);
                    double successfulPutCount = putSuccessCounter.count();
                    double averageLatency = successfulPutCount == 0 ? 0 : (totalPutResponseTime / successfulPutCount);
                    return Math.round(averageLatency * 10.0) / 10.0;
                })
                .description("Average latency of PUT operations in milliseconds")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        Gauge.builder("couchbase.get.average_latency", () -> {
                    double totalGetResponseTime = getTimer.totalTime(TimeUnit.MILLISECONDS);
                    double successfulGetCount = getSuccessCounter.count();
                    double averageLatency = successfulGetCount == 0 ? 0 : (totalGetResponseTime / successfulGetCount);
                    return Math.round(averageLatency * 10.0) / 10.0;
                })
                .description("Average latency of GET operations in milliseconds")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        Gauge.builder("couchbase.average_response_time", () -> {
                    double totalResponseTime = putTimer.totalTime(TimeUnit.MILLISECONDS) + getTimer.totalTime(TimeUnit.MILLISECONDS);
                    double totalSuccessCount = putSuccessCounter.count() + getSuccessCounter.count();
                    double averageResponseTime = totalSuccessCount == 0 ? 0 : (totalResponseTime / totalSuccessCount);
                    return Math.round(averageResponseTime * 10.0) / 10.0;
                })
                .description("Overall average response time for PUT and GET operations in milliseconds")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        Gauge.builder("couchbase.transactions_per_second (TPS)", () -> {
                    double totalSuccessfulTransactions = putSuccessCounter.count() + getSuccessCounter.count();
                    double totalOperationTime = putTimer.totalTime(TimeUnit.MILLISECONDS) + getTimer.totalTime(TimeUnit.MILLISECONDS);
                    return totalOperationTime == 0 ? 0 : Math.round((totalSuccessfulTransactions / (totalOperationTime / 1000.0)) * 10.0) / 10.0;
                })
                .description("Total successful transactions processed per second (PUT + GET)")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        Gauge.builder("couchbase.total_error_rate", () -> {
                    double totalSuccessfulTransactions = putSuccessCounter.count() + getSuccessCounter.count();
                    double totalFailedTransactions = putFailureCounter.count() + getFailureCounter.count();
                    double totalTransactions = totalSuccessfulTransactions + totalFailedTransactions;

                    return totalTransactions == 0 ? 0 : (totalFailedTransactions / totalTransactions) * 100;
                })
                .description("Total error rate for PUT and GET operations in percentage")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        Gauge.builder("couchbase.put.max_latency", () -> {
                    double currentMaxPutLatency = putTimer.totalTime(TimeUnit.MILLISECONDS);
                    return currentMaxPutLatency == 0 ? 0 : Math.round(currentMaxPutLatency);
                })
                .description("Maximum latency of PUT operations in milliseconds")
                .tag("scenario", scenarioId)
                .register(meterRegistry);
        Gauge.builder("couchbase.get.max_latency", () -> {
                    double currentMaxGetLatency = getTimer.totalTime(TimeUnit.MILLISECONDS);
                    return currentMaxGetLatency == 0 ? 0 : Math.round(currentMaxGetLatency);
                })
                .description("Maximum latency of GET operations in milliseconds")
                .tag("scenario", scenarioId)
                .register(meterRegistry);

        Gauge.builder("couchbase.total_successful_operations", () -> {
                    double totalSuccessfulPut = putSuccessCounter.count();
                    double totalSuccessfulGet = getSuccessCounter.count();
                    return totalSuccessfulPut + totalSuccessfulGet;
                })
                .description("Total number of successful PUT and GET operations executed")
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