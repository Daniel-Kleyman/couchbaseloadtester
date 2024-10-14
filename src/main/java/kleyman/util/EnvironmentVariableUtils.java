package kleyman.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling environment variables.
 */
public class EnvironmentVariableUtils {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentVariableUtils.class);

    public static String getEnv(String variable) {
        String value = System.getenv(variable);
        if (value == null || value.isEmpty()) {
            logger.error("Environment variable {} not set or empty", variable);
            throw new IllegalArgumentException("Environment variable " + variable + " not set or empty");
        }
        logger.info("Retrieved environment variable: {}", variable);
        return value;
    }
}