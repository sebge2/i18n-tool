package be.sgerard.i18n.model.i18n.file;

import lombok.Getter;

import java.io.File;

/**
 * Location of a {@link ScannedBundleFile bundle file}.
 *
 * @author Sebastien Gerard
 */
@Getter
public class ScannedBundleFileLocation {

    /**
     * The directory containing bundle files.
     */
    private final File directory;

    /**
     * The bundle name (based on file names).
     */
    private final String name;

    public ScannedBundleFileLocation(File directory, String name) {
        this.directory = directory;
        this.name = name;
    }
}
