package be.sgerard.i18n.service.i18n.file;

import org.springframework.util.StringUtils;

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

    public static File removeParentFile(File parentFile, File fullFile) {
        return fullFile.toPath().subpath(parentFile.toPath().getNameCount(), fullFile.toPath().getNameCount()).toFile();
    }

    public static String mapToNullIfEmpty(String value){
        return StringUtils.isEmpty(value) ? null : value;
    }
}
