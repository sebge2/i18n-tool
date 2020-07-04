package be.sgerard.i18n.model.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationKeyPatternDto;
import be.sgerard.i18n.model.i18n.dto.TranslationSearchCriterion;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Request asking the listing of paginated translations.
 *
 * @author Sebastien Gerard
 */
public class TranslationsSearchRequest {

    public static Builder builder() {
        return new Builder();
    }

    private final List<String> workspaces;
    private final List<String> locales;
    private final List<String> sortBy;
    private final TranslationSearchCriterion criterion;
    private final TranslationKeyPatternDto keyPattern;
    private final int maxTranslations;
    private final String lastKey;

    private TranslationsSearchRequest(Builder builder) {
        workspaces = unmodifiableList(builder.workspaces);
        locales = unmodifiableList(builder.locales);
        sortBy = unmodifiableList(builder.sortBy);
        criterion = builder.criterion;
        keyPattern = builder.keyPattern;
        maxTranslations = builder.maxTranslations;
        lastKey = builder.lastKey;
    }

    /**
     * Returns {@link WorkspaceEntity workspaces} to search inside.
     */
    public List<String> getWorkspaces() {
        return workspaces;
    }

    /**
     * Returns {@link TranslationLocaleEntity locales} of translations to look for.
     */
    public Collection<String> getLocales() {
        return locales;
    }

    /**
     * Returns the list of fields to sort by.
     */
    public List<String> getSortBy() {
        return sortBy;
    }

    /**
     * Returns the {@link TranslationSearchCriterion criterion} that translations must have.
     */
    public TranslationSearchCriterion getCriterion() {
        return criterion;
    }

    /**
     * Returns the {@link TranslationKeyPatternDto pattern} to use of keys to retrieve.
     */
    public Optional<TranslationKeyPatternDto> getKeyPattern() {
        return Optional.ofNullable(keyPattern);
    }

    /**
     * Returns the maximum number of translations for the next page.
     */
    public int getMaxTranslations() {
        return maxTranslations;
    }

    /**
     * Returns the last key contained in the previous page (nothing if it's the first page).
     *
     * @see TranslationsPageDto#getLastKey()
     */
    public Optional<String> getLastKey() {
        return Optional.ofNullable(lastKey);
    }

    /**
     * Builder of {@link TranslationsSearchRequest search request}.
     */
    public static final class Builder {

        private final List<String> workspaces = new ArrayList<>();
        private final List<String> locales = new ArrayList<>();
        private final List<String> sortBy = new ArrayList<>();
        private TranslationSearchCriterion criterion;
        private TranslationKeyPatternDto keyPattern;
        private int maxTranslations;
        private String lastKey;

        private Builder() {
        }

        public Builder workspaces(Collection<String> workspaces) {
            this.workspaces.addAll(workspaces);
            return this;
        }

        public Builder workspaces(String... workspaces) {
            return workspaces(asList(workspaces));
        }

        public Builder locales(Collection<String> locales) {
            this.locales.addAll(locales);
            return this;
        }

        public Builder locales(String... locales) {
            return locales(asList(locales));
        }

        public Builder sortBy(Collection<String> sortBy) {
            this.sortBy.addAll(sortBy);
            return this;
        }

        public Builder sortBy(String... sortBy) {
            return sortBy(asList(sortBy));
        }

        public Builder criterion(TranslationSearchCriterion criterion) {
            this.criterion = criterion;
            return this;
        }

        public Builder keyPattern(TranslationKeyPatternDto keyPattern) {
            this.keyPattern = keyPattern;
            return this;
        }

        public Builder maxTranslations(int maxTranslations) {
            this.maxTranslations = maxTranslations;

            return this;
        }

        public Builder lastKey(String lastKey) {
            this.lastKey = lastKey;
            return this;
        }

        public TranslationsSearchRequest build() {
            return new TranslationsSearchRequest(this);
        }
    }


}
