package kleyman.testrunner;

import kleyman.config.CouchbaseConnectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouchbaseTestRunnerTest {
    private CouchbaseTestRunner testRunner;

    @BeforeEach
    void setUp() {
        testRunner = new CouchbaseTestRunner();
    }

    @Test
    @DisplayName("Test no exceptions thrown when runTests is called")
    void GivenTestRunner_WhenRunTestsCalled_ThenNoExceptionsThrown() {
        // Given
        // Initialize the test runner

        // When and Then
        assertDoesNotThrow(() -> testRunner.runTests(), "runTests should not throw any exceptions.");
    }

    @Test
    @DisplayName("Test number of tests run is zero initially")
    void GivenTestRunner_WhenRunTestsCalled_ThenNumberOfTestsRunIsZeroInitially() {
        // Given
        // Initialize the test runner

        // When
        testRunner.runTests();

        // Then
        assertEquals(0, testRunner.numberOfTestRun, "Number of tests run should be zero since runThreadPoolTest and runConnectionPoolTest are not called.");
    }

    @Test
    @DisplayName("Test handles initialization exception gracefully")
    void GivenConnectionManager_WhenCreating_ThenShouldHandleInitializationException() {
        // Given
        CouchbaseTestRunner runner = Mockito.spy(testRunner);
        CouchbaseConnectionManager mockManager = mock(CouchbaseConnectionManager.class);

        doThrow(new RuntimeException("Initialization failed"))
                .when(mockManager)
                .initializeBucket();

        // When
        // Call the method which should execute without throwing exceptions

        // Then
        assertDoesNotThrow(() -> {
            runner.runTests();
        }, "runTests should not throw exceptions even when initializeBucket fails.");
    }
}
