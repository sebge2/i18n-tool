package be.sgerard.poc.githuboauth.model.i18n.file;

import be.sgerard.poc.githuboauth.model.i18n.BundleType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastien Gerard
 */
public class ScannedBundleFileDto {

    public static ScannedBundleFileDto merge(ScannedBundleFileDto first, ScannedBundleFileDto second){
        if (first == null) {
            return second;
        } else {
            if (second == null) {
                return first;
            } else {
                final List<File> files = new ArrayList<>(first.getFiles());
                files.addAll(second.getFiles());

                return new ScannedBundleFileDto(first.getName(), BundleType.JAVA, first.getLocationDirectory(), files);
            }
        }
    }

    private final String name;
    private final BundleType type;
    private final File locationDirectory;
    private final List<File> files;

    public ScannedBundleFileDto(String name,
                                BundleType type,
                                File locationDirectory,
                                List<File> files) {
        this.name = name;
        this.type = type;
        this.locationDirectory = locationDirectory;
        this.files = files;
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

    public List<File> getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return "ScannedBundleFileDto(" +name + ":" + locationDirectory + ")";
    }
}
