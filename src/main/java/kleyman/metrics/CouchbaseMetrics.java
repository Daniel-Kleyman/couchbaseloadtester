package kleyman.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Metrics collector for Couchbase operations.
 * This class manages metrics for PUT and GET operations using Micrometer,
 * including counters for success and failure rates, and timers for operation latencies.
 *
 * <p>It provides methods to increment counters, record latencies, and calculate metrics
 * such as average latency, transactions per second, error rates, and maximum latencies
 * for performance monitoring and analysis.</p>
 */
@Getter
public class CouchbaseMetrics {

    private static final Logger logger = LoggerFactory.getLogger(CouchbaseMetrics.class);
    private final Counter putSuccessCounter;
    private final Counter putFailureCounter;
    private final Counter getSuccessCounter;
    private final Counter getFailureCounter;
    private final Timer putTimer;
    private final Timer getTimer;
    private final int threadSize;
    private final String jsonSize;
    private final boolean uniqueKeys;

    public CouchbaseMetrics(MeterRegistry meterRegistry, String scenarioId, int threadSize, String jsonSize, boolean uniqueKeys) {
        logger.info("Starting collection of metrics");

        this.threadSize = threadSize;
        this.jsonSize = jsonSize;
        this.uniqueKeys = uniqueKeys;
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

    public double getAveragePutLatency() {
        double totalPutResponseTime = putTimer.totalTime(TimeUnit.MILLISECONDS);
        double successfulPutCount = putSuccessCounter.count();
        return successfulPutCount == 0 ? 0 : Math.round((totalPutResponseTime / successfulPutCount) * 10.0) / 10.0;
    }

    public double getAverageGetLatency() {
        double totalGetResponseTime = getTimer.totalTime(TimeUnit.MILLISECONDS);
        double successfulGetCount = getSuccessCounter.count();
        return successfulGetCount == 0 ? 0 : Math.round((totalGetResponseTime / successfulGetCount) * 10.0) / 10.0;
    }

    public double getOverallAverageResponseTime() {
        double totalResponseTime = putTimer.totalTime(TimeUnit.MILLISECONDS) + getTimer.totalTime(TimeUnit.MILLISECONDS);
        double totalSuccessfulCount = putSuccessCounter.count() + getSuccessCounter.count();
        return totalSuccessfulCount == 0 ? 0 : Math.round((totalResponseTime / totalSuccessfulCount) * 10.0) / 10.0;
    }

    public double getTransactionsPerSecond() {
        double totalSuccessfulTransactions = putSuccessCounter.count() + getSuccessCounter.count();
        double totalOperationTime = putTimer.totalTime(TimeUnit.MILLISECONDS) + getTimer.totalTime(TimeUnit.MILLISECONDS);
        return totalOperationTime == 0 ? 0 : Math.round((totalSuccessfulTransactions / (totalOperationTime / 1000.0)) * 10.0) / 10.0;
    }

    public double getTotalErrorRate() {
        double totalSuccessfulTransactions = putSuccessCounter.count() + getSuccessCounter.count();
        double totalFailedTransactions = putFailureCounter.count() + getFailureCounter.count();
        double totalTransactions = totalSuccessfulTransactions + totalFailedTransactions;

        return totalTransactions == 0 ? 0 : (totalFailedTransactions / totalTransactions) * 100;
    }

    public double getTotalSuccessfulOperations() {
        double totalSuccessfulPut = putSuccessCounter.count();
        double totalSuccessfulGet = getSuccessCounter.count();
        return totalSuccessfulPut + totalSuccessfulGet;
    }
}