package be.sgerard.i18n.model.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;

import java.util.function.BiFunction;

/**
 * Type of bundle translation files.
 *
 * @author Sebastien Gerard
 */
public enum BundleType {

    /**
     * Java properties.
     */
    JAVA_PROPERTIES((location, name) -> String.format("%s/%s_*.properties", location, name)),

    /**
     * JSON files with ICU format.
     */
    JSON_ICU((location, name) -> String.format("%s/*.json", location));

    private final BiFunction<String, String, String> locationPathComputer;

    BundleType(BiFunction<String, String, String> locationPathComputer) {
        this.locationPathComputer = locationPathComputer;
    }

    /**
     * Returns the Ant path describing the location of the bundle having the current type. It's present at the specified location and has the specified name.
     *
     * @see BundleFileEntity#getLocation()
     * @see BundleFileEntity#getName()
     */
    public String getLocationPathPattern(String location, String name) {
        return locationPathComputer.apply(location.endsWith("/") ? location.substring(0, location.length() - 1) : location, name);
    }
}
