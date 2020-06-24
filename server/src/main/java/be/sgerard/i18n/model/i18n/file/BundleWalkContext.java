package be.sgerard.i18n.model.i18n.file;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

/**
 * Context used during walking around translation bundle files.
 *
 * @author Sebastien Gerard
 */
public class BundleWalkContext {

    private final TranslationRepositoryReadApi api;
    private final Map<BundleType, Predicate<Path>> inclusionPredicates;
    private final Set<TranslationLocaleEntity> locales;

    public BundleWalkContext(TranslationRepositoryReadApi api,
                             Map<BundleType, Predicate<Path>> inclusionPredicates,
                             Collection<TranslationLocaleEntity> locales) {
        this.inclusionPredicates = inclusionPredicates;
        this.api = api;
        this.locales = new HashSet<>(locales);
    }

    /**
     * Returns the {@link TranslationRepositoryReadApi API} to use for walking and reading files.
     */
    public TranslationRepositoryReadApi getApi() {
        return api;
    }

    /**
     * Returns the maps associating bundles type to a predicate specifying whether a path is included.
     */
    public Map<BundleType, Predicate<Path>> getInclusionPredicates() {
        return inclusionPredicates;
    }

    /**
     * Returns whether the specified path can be walked trough it.
     */
    public boolean canWalkTrough(BundleType bundleType, Path path){
        return getInclusionPredicates().getOrDefault(bundleType, (p -> true)).test(path);
    }

    /**
     * Returns all the locales to look for.
     */
    public Set<TranslationLocaleEntity> getLocales() {
        return locales;
    }
}
