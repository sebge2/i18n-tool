package be.sgerard.i18n.model.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntryEntity;
import lombok.Getter;

import java.io.File;
import java.util.*;

/**
 * A translation bundle file that have been scanned.
 *
 * @author Sebastien Gerard
 */
@Getter
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

    /**
     * The bundle name (based on file names).
     */
    private final String name;

    /**
     * The {@link BundleType type} of bundle.
     */
    private final BundleType type;

    /**
     * The directory containing bundle files.
     */
    private final File locationDirectory;

    /**
     * All the {@link ScannedBundleFileEntry files} composing the bundle associated by their locales.
     */
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

    @Override
    public String toString() {
        return "ScannedBundleFile(" + name + ":" + locationDirectory + ")";
    }

    /**
     * Finds the {@link ScannedBundleFileEntry entry} for the specified locale id.
     */
    private static Optional<BundleFileEntryEntity> getFileEntry(BundleFileEntity entity, String locale) {
        return entity.getFiles().stream().filter(file -> Objects.equals(file.getLocale(), locale)).findFirst();
    }
}
