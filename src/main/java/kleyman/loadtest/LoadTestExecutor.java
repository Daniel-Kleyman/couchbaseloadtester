package kleyman.loadtest;

/**
 * Interface representing an executor for database load test scenarios.
 *
 * @param <T> the type of the data handled by the database service
 */
public interface LoadTestExecutor<T> {

    void executeLoadTest();
}
