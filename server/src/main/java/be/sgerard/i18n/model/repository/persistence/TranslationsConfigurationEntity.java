package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.i18n.persistence.BundleConfigurationEntity;

import javax.persistence.*;
import java.util.*;

import static java.util.Collections.unmodifiableCollection;

/**
 * Configuration of how translations are retrieved and manage for a particular repository.
 *
 * @author Sebastien Gerard
 */
@Entity
public class TranslationsConfigurationEntity {

    @Id
    private String id;

    @OneToOne(optional = false)
    @JoinColumn(name = "translations_configuration")
    private RepositoryEntity repository;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private final Collection<BundleConfigurationEntity> bundles = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "repository_translations_ignored_keys", joinColumns = @JoinColumn(name = "configuration_id"))
    private List<String> ignoredKeys = new ArrayList<>();

    TranslationsConfigurationEntity() {
    }

    public TranslationsConfigurationEntity(RepositoryEntity repository) {
        this.id = UUID.randomUUID().toString();
        this.repository = repository;
    }

    /**
     * Returns the unique id of this configuration.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this configuration.
     */
    public TranslationsConfigurationEntity setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the associated {@link RepositoryEntity repository}.
     */
    public RepositoryEntity getRepository() {
        return repository;
    }

    /**
     * Sets the associated {@link RepositoryEntity repository}.
     */
    public TranslationsConfigurationEntity setRepository(RepositoryEntity repository) {
        this.repository = repository;
        return this;
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
    public void addBundlesConfiguration(BundleConfigurationEntity bundle) {
        this.bundles.add(bundle);
    }

    /**
     * Adds the {@link BundleConfigurationEntity configuration of bundles}.
     */
    public TranslationsConfigurationEntity addBundlesConfiguration(Collection<BundleConfigurationEntity> bundles) {
        this.bundles.addAll(bundles);
        return this;
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

    /**
     * Creates a deep copy of this entity using the specified repository as the reference to use.
     */
    public TranslationsConfigurationEntity deepCopy(RepositoryEntity repository) {
        return new TranslationsConfigurationEntity(repository)
                .setId(getId())
                .setIgnoredKeys(new ArrayList<>(getIgnoredKeys()))
                .addBundlesConfiguration(getBundles());
    }
}
