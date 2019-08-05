package be.sgerard.poc.githuboauth.model.i18n.file;

import be.sgerard.poc.githuboauth.model.i18n.BundleType;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleFileEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static java.util.stream.Collectors.toSet;

/**
 * @author Sebastien Gerard
 */
public class ScannedBundleFileDto {

    public static ScannedBundleFileDto merge(ScannedBundleFileDto first, ScannedBundleFileDto second) {
        if (first == null) {
            return second;
        } else {
            if (second == null) {
                return first;
            } else {
                final List<File> files = new ArrayList<>(first.getFiles());
                files.addAll(second.getFiles());

                final List<Locale> locales = new ArrayList<>(first.getLocales());
                locales.addAll(second.getLocales());

                return new ScannedBundleFileDto(
                    first.getName(),
                    BundleType.JAVA,
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
    private final Collection<Locale> locales;
    private final Collection<File> files;

    public ScannedBundleFileDto(String name,
                                BundleType type,
                                File locationDirectory,
                                Collection<Locale> locales,
                                Collection<File> files) {
        this.name = name;
        this.type = type;
        this.locationDirectory = locationDirectory;
        this.locales = locales;
        this.files = files;
    }

    public ScannedBundleFileDto(BundleFileEntity entity) {
        this(
            entity.getName(),
            entity.getType(),
            new File(entity.getLocation()),
            entity.getLocales(),
            entity.getFiles().stream().map(File::new).collect(toSet())
        );
    }

    public String getName() {
        return name;
    }

    public BundleType getType() {
        return type;
    }

    public File getLocationDirectory() {
        return locationDirectory;
    }

    public Collection<Locale> getLocales() {
        return locales;
    }

    public Collection<File> getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return "ScannedBundleFileDto(" + name + ":" + locationDirectory + ")";
    }
}
