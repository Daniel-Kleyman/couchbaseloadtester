package kleyman.util;

import com.couchbase.client.java.json.JsonObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonFileReaderUtilsTest {

    @Test
    @DisplayName("Test that valid JSON file path returns JsonObject")
    void Given_ValidJsonFilePath_When_ReadJsonFromFileCalled_Then_ReturnsJsonObject() throws IOException {
        // Given:
        Path filePath = Paths.get("src/test/resources/validJson.json");

        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + filePath.toAbsolutePath());
        }

        // When:
        JsonObject jsonObject = JsonFileReaderUtils.readJsonFromFile(filePath.toString());

        // Then:
        assertEquals("value", jsonObject.getString("key"));
    }

    @Test
    @DisplayName("Test that invalid JSON file path throws IOException")
    void Given_InvalidJsonFilePath_When_ReadJsonFromFileCalled_Then_ThrowsIOException() {
        // Given
        String filePath = "src/test/resources/invalidJson.json";

        // When / Then
        assertThrows(IOException.class, () -> JsonFileReaderUtils.readJsonFromFile(filePath));
    }

    @Test
    @DisplayName("Test that non-existent JSON file path throws IOException")
    void Given_NonExistentJsonFilePath_When_ReadJsonFromFileCalled_Then_ThrowsIOException() {
        // Given
        String filePath = "src/test/resources/nonExistent.json";

        // When / Then
        assertThrows(IOException.class, () -> JsonFileReaderUtils.readJsonFromFile(filePath));
    }
}
