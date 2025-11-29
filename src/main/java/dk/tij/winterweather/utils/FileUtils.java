package dk.tij.winterweather.utils;

import java.io.File;
import java.io.IOException;

public final class FileUtils {
    public static void createFileIfNotExistent(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException ignored) {}
    }
}
