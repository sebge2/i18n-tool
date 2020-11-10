package be.sgerard.i18n.support;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Bunch of utility methods for files.
 *
 * @author Sebastien Gerard
 */
public final class FileUtils {

    private FileUtils() {
    }

    /**
     * Creates a temporary directory with the specified prefix name.
     */
    public static File createTempDirectory(String prefix) {
        try {
            return Files.createTempDirectory(prefix).toFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create temporary directory.", e);
        }
    }

    /**
     * Deletes the specified directory (not necessarily empty).
     */
    public static void deleteDirectory(File directory) {
        try {
            org.apache.commons.io.FileUtils.forceDelete(directory);
        } catch (IOException e) {
            throw new RuntimeException("Cannot delete directory", e);
        }
    }
}
