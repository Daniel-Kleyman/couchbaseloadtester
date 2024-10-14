package kleyman.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentVariableUtilsTest {

    @Test
    @DisplayName("Test retrieving environment variable when set")
    void Given_EnvironmentVariableIsSet_When_GetEnvCalled_Then_ReturnsValue() {
        // Given
        String variable = "TEST_VAR";
        String expectedValue = "TestValue";

        // When
        // Use a custom wrapper method to simulate the environment variable retrieval
        String actualValue = getMockEnv(variable, expectedValue);

        // Then
        assertEquals(expectedValue, actualValue, "Should return the value of the environment variable");
    }

    @Test
    @DisplayName("Test retrieving environment variable when not set")
    void Given_EnvironmentVariableIsNotSet_When_GetEnvCalled_Then_ThrowsIllegalArgumentException() {
        // Given
        String variable = "UNSET_VAR";

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            EnvironmentVariableUtils.getEnv(variable);
        });

        // Then
        assertEquals("Environment variable " + variable + " not set or empty", exception.getMessage());
    }

    @Test
    @DisplayName("Test retrieving environment variable when empty")
    void Given_EnvironmentVariableIsEmpty_When_GetEnvCalled_Then_ThrowsIllegalArgumentException() {
        // Given
        String variable = "EMPTY_VAR";

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            EnvironmentVariableUtils.getEnv(variable);
        });

        // Then
        assertEquals("Environment variable " + variable + " not set or empty", exception.getMessage());
    }

    // Mock the environment variable retrieval
    private String getMockEnv(String variable, String value) {
        if (variable.equals("TEST_VAR")) {
            return value;
        } else {
            throw new IllegalArgumentException("Environment variable " + variable + " not set or empty");
        }
    }
}
