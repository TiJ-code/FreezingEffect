package dk.tij.freezingEffect.config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class MigrationReader {
    public static Map<String, String> readMigrations() {
        Map<String, String> migrations = new HashMap<>();

        try (InputStream inputStream = MigrationReader.class.getClassLoader()
                .getResourceAsStream("migrations.txt")) {
            if (inputStream == null)
                throw new FileNotFoundException("Migration file not found!");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                reader.lines().forEach(line -> {
                    String[] split = line.split(";");
                    migrations.put(split[0], split[1]);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return migrations;
    }
}
