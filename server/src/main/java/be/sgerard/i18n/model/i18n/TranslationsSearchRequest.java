package be.sgerard.i18n.model.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationKeyPatternDto;
import be.sgerard.i18n.model.i18n.dto.TranslationSearchCriterion;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
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
     * The list of fields to sort by.
     */
    private final List<String> sortBy;

    /**
     * The {@link TranslationSearchCriterion criterion} that translations must have.
     */
    private final TranslationSearchCriterion criterion;

    /**
     * The {@link TranslationKeyPatternDto pattern} to use of keys to retrieve.
     */
    private final TranslationKeyPatternDto keyPattern;

    /**
     * The current user id.
     */
    private final String currentUser;

    /**
     * The maximum number of translations for the next page.
     */
    private final Integer maxTranslations;

    /**
     * The index of the page to look for. The first page has the index 0.
     */
    private final int pageIndex;

    /**
     * @see #keyPattern
     */
    public Optional<TranslationKeyPatternDto> getKeyPattern() {
        return Optional.ofNullable(keyPattern);
    }

    /**
     * @see #maxTranslations
     */
    public Optional<Integer> getMaxTranslations() {
        return Optional.ofNullable(maxTranslations);
    }

    /**
     * Builder of {@link TranslationsSearchRequest search request}.
     */
    public static final class Builder {
    }
}
