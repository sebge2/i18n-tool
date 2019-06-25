package be.sgerard.poc.githuboauth.service.i18n.file;

import java.io.File;
import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
public final class TranslationFileUtils {

    private TranslationFileUtils() {
    }

    public static Stream<File> listFiles(File directory) {
        final File[] files = directory.listFiles();

        return (files != null) ? Stream.of(files) : Stream.empty();
    }
}
