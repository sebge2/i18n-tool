package be.sgerard.i18n.model.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toSet;

/**
 * A translation bundle file that have been scanned.
 *
 * @author Sebastien Gerard
 */
public class ScannedBundleFile {

    /**
     * Merges both bundle files together.
     */
    public static ScannedBundleFile merge(ScannedBundleFile first, ScannedBundleFile second) {
        if (first == null) {
            return second;
        } else {
            if (second == null) {
                return first;
            } else {
                final List<ScannedBundleFileEntry> files = new ArrayList<>(first.getFiles());
                files.addAll(second.getFiles());

                return new ScannedBundleFile(
                        first.getName(),
                        first.getType(),
                        first.getLocationDirectory(),
                        files
                );
            }
        }
    }

    private final String name;
    private final BundleType type;
    private final File locationDirectory;
    private final Collection<ScannedBundleFileEntry> files;

    public ScannedBundleFile(String name,
                             BundleType type,
                             File locationDirectory,
                             Collection<ScannedBundleFileEntry> files) {
        this.name = name;
        this.type = type;
        this.locationDirectory = locationDirectory;
        this.files = files;
    }

    public ScannedBundleFile(BundleFileEntity entity) {
        this(
                entity.getName(),
                entity.getType(),
                new File(entity.getLocation()),
                entity.getFiles().stream().map(file -> new ScannedBundleFileEntry(file.getLocale(), file.getJavaFile())).collect(toSet())
        );
    }

    /**
     * Returns the bundle name (based on file names).
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link BundleType type} of bundle.
     */
    public BundleType getType() {
        return type;
    }

    /**
     * Returns the directory containing bundle files.
     */
    public File getLocationDirectory() {
        return locationDirectory;
    }

    /**
     * Returns all the {@link ScannedBundleFileEntry files} composing the bundle associated by their locales.
     */
    public Collection<ScannedBundleFileEntry> getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return "ScannedBundleFile(" + name + ":" + locationDirectory + ")";
    }
}
