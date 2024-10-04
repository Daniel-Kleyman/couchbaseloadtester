package kleyman.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class for load testing scenarios.
 * This class defines various test scenarios for benchmarking Couchbase's key-value functionality.
 */
public class LoadTestConfig {
    private static final Logger logger = LoggerFactory.getLogger(LoadTestConfig.class); // Logger instance

    public static final List<TestScenario> SCENARIOS = List.of(
            new TestScenario(5, getEnv("JSON_BIG_PATH"), true),
            new TestScenario(5, getEnv("JSON_BIG_PATH"), false),
            new TestScenario(10, getEnv("JSON_BIG_PATH"), true),
            new TestScenario(10, getEnv("JSON_BIG_PATH"), false),
            new TestScenario(15, getEnv("JSON_BIG_PATH"), true),
            new TestScenario(15, getEnv("JSON_BIG_PATH"), false),
            new TestScenario(5, getEnv("JSON_SMALL_PATH"), true),
            new TestScenario(5, getEnv("JSON_SMALL_PATH"), false),
            new TestScenario(10, getEnv("JSON_SMALL_PATH"), true),
            new TestScenario(10, getEnv("JSON_SMALL_PATH"), false),
            new TestScenario(15, getEnv("JSON_SMALL_PATH"), true),
            new TestScenario(15, getEnv("JSON_SMALL_PATH"), false)
    );

    /**
     * Represents a test scenario for load testing.
     */
    public static class TestScenario {
        public final int threadCount;
        public final String jsonFilePath;
        public final boolean useUniqueKeys;

        /**
         * Constructs a TestScenario with the specified parameters.
         *
         * @param threadCount   the number of threads to use for the test
         * @param jsonFilePath  the path to the JSON file
         * @param useUniqueKeys whether to use unique keys for the test
         */
        public TestScenario(int threadCount, String jsonFilePath, boolean useUniqueKeys) {
            this.threadCount = threadCount;
            this.jsonFilePath = jsonFilePath;
            this.useUniqueKeys = useUniqueKeys;
            logger.info("Created TestScenario: {} threads, JSON file: {}, Unique keys: {}",
                    threadCount, jsonFilePath, useUniqueKeys);
        }
    }

    /**
     * Retrieves the value of an environment variable.
     *
     * @param variable the name of the environment variable
     * @return the value of the environment variable
     * @throws IllegalArgumentException if the environment variable is not set or is empty
     */
    private static String getEnv(String variable) {
        String value = System.getenv(variable);
        if (value == null || value.isEmpty()) {
            logger.error("Environment variable {} not set or empty", variable);
            throw new IllegalArgumentException("Environment variable " + variable + " not set or empty");
        }
        logger.info("Retrieved environment variable {}: {}", variable, value);
        return value;
    }
}
