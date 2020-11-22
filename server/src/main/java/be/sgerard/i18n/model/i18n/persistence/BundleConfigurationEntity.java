package be.sgerard.i18n.model.i18n.persistence;

import be.sgerard.i18n.model.i18n.BundleType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.util.AntPathMatcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Configurations of bundles of a repository.
 *
 * @author Sebastien Gerard
 */
@Getter
@Setter
@Accessors(chain = true)
public class BundleConfigurationEntity {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * The type of bundle associated to this configuration.
     */
    private BundleType bundleType;

    /**
     * All the paths that are included when scanning bundles of this type.
     * By default all paths are included. If paths have been specified only those paths will be scanned,
     * expected if they have been {@link #getIgnoredPaths() ignored}.
     */
    private List<String> includedPaths = new ArrayList<>();

    /**
     * All the paths that are ignored when scanning bundles of this type.
     * <p>
     * Note that ignored paths have a bigger priority over {@link #getIncludedPaths() included paths}.
     */
    private List<String> ignoredPaths = new ArrayList<>();

    @PersistenceConstructor
    BundleConfigurationEntity() {
    }

    public BundleConfigurationEntity(BundleType bundleType) {
        this.bundleType = bundleType;
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
