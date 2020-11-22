package be.sgerard.i18n.model.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationKeyPatternDto;
import be.sgerard.i18n.model.i18n.dto.TranslationSearchCriterion;
import be.sgerard.i18n.model.i18n.dto.TranslationValueRestrictionDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchPageSpecDto;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Optional;

/**
 * Request asking the listing of paginated translations.
 *
 * @author Sebastien Gerard
 */
@Getter
@Builder(builderClassName = "Builder")
public class TranslationsSearchRequest {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@link WorkspaceEntity Workspaces} to search inside.
     */
    @Singular
    private final List<String> workspaces;

    /**
     * {@link TranslationLocaleEntity Locales} of translations to look for.
     */
    @Singular
    private final List<String> locales;

    /**
     * {@link BundleFileEntity Bundle files} of translations to look for.
     */
    @Singular
    private final List<String> bundleFiles;

    /**
     * The {@link TranslationSearchCriterion criterion} that translations must have.
     */
    private final TranslationSearchCriterion criterion;

    /**
     * The {@link TranslationKeyPatternDto pattern} to use of keys to retrieve.
     */
    private final TranslationKeyPatternDto keyPattern;

    /**
     * The {@link TranslationValueRestrictionDto restriction} to apply on translation values.
     */
    private final TranslationValueRestrictionDto valueRestriction;

    /**
     * The current user id.
     */
    private final String currentUser;

    /**
     * The maximum number of bundle keys for the next page.
     */
    private final Integer maxKeys;

    /**
     * Specification of the page to search for.
     */
    private final TranslationsSearchPageSpecDto pageSpec;

    /**
     * @see #keyPattern
     */
    public Optional<TranslationKeyPatternDto> getKeyPattern() {
        return Optional.ofNullable(keyPattern);
    }

    /**
     * @see #valueRestriction
     */
    public Optional<TranslationValueRestrictionDto> getValueRestriction() {
        return Optional.ofNullable(valueRestriction);
    }

    /**
     * @see #maxKeys
     */
    public Optional<Integer> getMaxKeys() {
        return Optional.ofNullable(maxKeys);
    }

    /**
     * @see #pageSpec
     */
    public Optional<TranslationsSearchPageSpecDto> getPageSpec() {
        return Optional.ofNullable(pageSpec);
    }

    /**
     * Builder of {@link TranslationsSearchRequest search request}.
     */
    public static final class Builder {
    }
}
