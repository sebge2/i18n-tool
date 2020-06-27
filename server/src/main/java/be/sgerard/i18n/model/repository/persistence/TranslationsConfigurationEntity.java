package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleConfigurationEntity;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.*;

import static java.util.Collections.unmodifiableCollection;

/**
 * Configuration of how translations are retrieved and manage for a particular repository.
 *
 * @author Sebastien Gerard
 */
public class TranslationsConfigurationEntity {

    private Collection<BundleConfigurationEntity> bundles = new HashSet<>();

    private List<String> ignoredKeys = new ArrayList<>();

    @PersistenceConstructor
    TranslationsConfigurationEntity() {
    }

    /**
     * Returns the {@link BundleConfigurationEntity configurations of bundles}.
     */
    public Collection<BundleConfigurationEntity> getBundles() {
        return unmodifiableCollection(bundles);
    }

    /**
     * Adds the {@link BundleConfigurationEntity configuration of bundles}.
     */
    public void addBundles(BundleConfigurationEntity bundle) {
        this.bundles.add(bundle);
    }

    /**
     * Adds the {@link BundleConfigurationEntity configuration of bundles}.
     */
    public TranslationsConfigurationEntity setBundles(Collection<BundleConfigurationEntity> bundles) {
        this.bundles = bundles;
        return this;
    }

    /**
     * Returns the {@link BundleConfigurationEntity bundle configuration} for the specified {@link BundleType type}.
     */
    public Optional<BundleConfigurationEntity> getBundle(BundleType bundleType) {
        return getBundles().stream()
                .filter(bundle -> bundle.getBundleType() == bundleType)
                .findFirst();
    }

    /**
     * Returns the {@link BundleConfigurationEntity bundle configuration} for the specified {@link BundleType type}.
     */
    public BundleConfigurationEntity getBundleOrDie(BundleType bundleType) {
        return getBundle(bundleType)
                .orElseThrow(() -> new IllegalArgumentException("There is no configuration for type [" + bundleType + "]."));
    }

    /**
     * Returns the fully-qualified names of keys to ignore.
     */
    public List<String> getIgnoredKeys() {
        return ignoredKeys;
    }

    /**
     * Sets the fully-qualified names of keys to ignore.
     */
    public TranslationsConfigurationEntity setIgnoredKeys(List<String> ignoredKeys) {
        this.ignoredKeys = ignoredKeys;
        return this;
    }
}
