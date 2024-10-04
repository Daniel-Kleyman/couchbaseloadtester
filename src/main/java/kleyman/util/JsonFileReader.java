package kleyman.util;

import com.couchbase.client.java.json.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonFileReader {
    public static JsonObject readJsonFromFile(String filePath) throws IOException {
        String content = Files.readString(Paths.get(filePath), StandardCharsets.UTF_8);
        return JsonObject.fromJson(content);
    }
}

