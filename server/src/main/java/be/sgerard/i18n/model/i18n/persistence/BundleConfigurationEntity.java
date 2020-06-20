package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import org.springframework.util.AntPathMatcher;

import javax.persistence.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Configurations of bundles of a repository.
 *
 * @author Sebastien Gerard
 */
@Entity
public class BundleConfigurationEntity {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private BundleType bundleType;

    @ManyToOne
    private RepositoryEntity repository;

    @ElementCollection
    @CollectionTable(name = "repository_translations_included_path", joinColumns = @JoinColumn(name = "bundle_configuration_id"))
    private List<String> includedPaths = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "repository_translations_ignored_path", joinColumns = @JoinColumn(name = "bundle_configuration_id"))
    private List<String> ignoredPaths = new ArrayList<>();

    BundleConfigurationEntity() {
    }
// TODO
    public BundleConfigurationEntity(BundleType bundleType, RepositoryEntity repository) {
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
    public BundleConfigurationEntity setId(String id) {
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
    public BundleConfigurationEntity setBundleType(BundleType bundleType) {
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
    public BundleConfigurationEntity setRepository(RepositoryEntity repository) {
        this.repository = repository;
        return this;
    }

    /**
     * Returns all the paths that are included when scanning bundles of this type.
     * By default all paths are included. If paths have been specified only those paths will be scanned,
     * expected if they have been {@link #getIgnoredPaths() ignored}.
     */
    public List<String> getIncludedPaths() {
        return includedPaths;
    }

    /**
     * Sets all the paths that are included when scanning bundles of this type.
     */
    public BundleConfigurationEntity setIncludedPaths(List<String> includedPaths) {
        this.includedPaths = includedPaths;
        return this;
    }

    /**
     * Returns all the paths that are ignored when scanning bundles of this type.
     * <p>
     * Note that ignored paths have a bigger priority over {@link #getIncludedPaths() included paths}.
     */
    public List<String> getIgnoredPaths() {
        return ignoredPaths;
    }

    /**
     * Sets all the paths that are ignored when scanning bundles of this type.
     */
    public BundleConfigurationEntity setIgnoredPaths(List<String> ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
        return this;
    }

    /**
     * Returns whether the specified path is included.
     */
    public boolean isIncluded(Path path) {
        final String pathString = path.toString();

        if (!getIncludedPaths().isEmpty()) {
            final boolean included = getIncludedPaths()
                    .stream()
                    .anyMatch(includedPathPattern -> PATH_MATCHER.match(includedPathPattern, pathString));

            if (!included) {
                return false;
            }
        }

        return getIgnoredPaths()
                .stream()
                .noneMatch(ignoredPathPattern -> PATH_MATCHER.match(ignoredPathPattern, pathString));
    }
}
