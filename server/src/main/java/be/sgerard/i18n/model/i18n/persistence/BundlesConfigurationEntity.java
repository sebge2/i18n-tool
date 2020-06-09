package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Configurations of bundles of a repository.
 *
 * @author Sebastien Gerard
 */
@Entity
public class BundlesConfigurationEntity {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private BundleType bundleType;

    @ManyToOne
    private RepositoryEntity repository;

    @ElementCollection
    private List<String> ignoredPaths = new ArrayList<>();

    BundlesConfigurationEntity() {
    }

    BundlesConfigurationEntity(BundleType bundleType, RepositoryEntity repository) {
        this.id = UUID.randomUUID().toString();
        this.bundleType = bundleType;
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
    public BundlesConfigurationEntity setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the type of bundle associated to this configuration.
     */
    public BundleType getBundleType() {
        return bundleType;
    }

    /**
     * Sets the type of bundle associated to this configuration.
     */
    public BundlesConfigurationEntity setBundleType(BundleType bundleType) {
        this.bundleType = bundleType;
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
    public BundlesConfigurationEntity setRepository(RepositoryEntity repository) {
        this.repository = repository;
        return this;
    }

    /**
     * Returns all the paths that are ignored when scanning bundles of this type.
     */
    public List<String> getIgnoredPaths() {
        return ignoredPaths;
    }

    /**
     * Sets all the paths that are ignored when scanning bundles of this type.
     */
    public BundlesConfigurationEntity setIgnoredPaths(List<String> ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
        return this;
    }
}
