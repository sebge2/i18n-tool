package be.sgerard.i18n.model.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
                final List<File> files = new ArrayList<>(first.getFiles());
                files.addAll(second.getFiles());

                final List<TranslationLocaleEntity> locales = new ArrayList<>(first.getLocales());
                locales.addAll(second.getLocales());

                return new ScannedBundleFile(
                    first.getName(),
                    first.getType(),
                    first.getLocationDirectory(),
                    locales,
                    files
                );
            }
        }
    }

    private final String name;
    private final BundleType type;
    private final File locationDirectory;
    private final Collection<TranslationLocaleEntity> locales;
    private final Collection<File> files;

    public ScannedBundleFile(String name,
                             BundleType type,
                             File locationDirectory,
                             Collection<TranslationLocaleEntity> locales,
                             Collection<File> files) {
        this.name = name;
        this.type = type;
        this.locationDirectory = locationDirectory;
        this.locales = locales;
        this.files = files;
    }

    public ScannedBundleFile(BundleFileEntity entity) {
        this(
            entity.getName(),
            entity.getType(),
            new File(entity.getLocation()),
            entity.getLocales(),
            entity.getFiles().stream().map(File::new).collect(toSet())
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
     * Returns all the {@link Locale locales} composing the bundle.
     */
    public Collection<TranslationLocaleEntity> getLocales() {
        return locales;
    }

    /**
     * Returns all the files composing the bundle.
     */
    public Collection<File> getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return "ScannedBundleFile(" + name + ":" + locationDirectory + ")";
    }
}
