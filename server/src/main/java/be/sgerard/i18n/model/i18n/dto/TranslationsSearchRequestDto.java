package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.util.Assert;

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
@Schema(name = "TranslationsSearchRequest", description = "Request asking the listing of paginated translations.")
@JsonDeserialize(builder = TranslationsSearchRequestDto.Builder.class)
public class TranslationsSearchRequestDto {

    /**
     * Default number of translation keys.
     */
    public static final int DEFAULT_MAX_KEYS = 50;

    /**
     * Maximum number of translation keys.
     */
    public static final int MAX_ALLOWED_KEYS = 500;

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Search translations only in those workspaces", required = true)
    private final List<String> workspaces;

    @Schema(description = "Search translations only in those translations locale ids.")
    private final List<String> locales;

    @Schema(description = "Specify the criterion that translations must have.")
    private final TranslationSearchCriterion criterion;

    @Schema(description = "The pattern to use of keys to retrieve.")
    private final TranslationKeyPatternDto keyPattern;

    @Schema(description = "The maximum number of keys for the next page.")
    private final int maxKeys;

    @Schema(description = "The index of the page to look for (the first page has the index 0)")
    private final int pageIndex;

    private TranslationsSearchRequestDto(Builder builder) {
        workspaces = unmodifiableList(builder.workspaces);
        locales = unmodifiableList(builder.locales);
        criterion = (builder.criterion != null) ? builder.criterion : TranslationSearchCriterion.ALL;
        keyPattern = builder.keyPattern;
        maxKeys = (builder.maxKeys != null) ? builder.maxKeys : DEFAULT_MAX_KEYS;
        pageIndex = builder.pageIndex;
    }

    /**
     * Returns {@link WorkspaceEntity#getId() ids} of workspaces to search inside.
     */
    public List<String> getWorkspaces() {
        return workspaces;
    }

    /**
     * Returns {@link TranslationLocaleEntity#getId() ids} of locales of translations to look for.
     */
    public List<String> getLocales() {
        return locales;
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
     * Returns the maximum number of keys for the next page.
     */
    public int getMaxKeys() {
        return maxKeys;
    }

    /**
     * Returns the index of the page to look for. The first page has the index 0.
     *
     * @see TranslationsPageDto#getPageIndex()
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * Builder of {@link TranslationsSearchRequestDto translations search request}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private final List<String> workspaces = new ArrayList<>();
        private final List<String> locales = new ArrayList<>();
        private TranslationSearchCriterion criterion;
        private TranslationKeyPatternDto keyPattern;
        private Integer maxKeys;
        private int pageIndex;

        private Builder() {
        }

        @JsonProperty("workspaces")
        public Builder workspaces(Collection<String> workspaces) {
            this.workspaces.addAll(workspaces);
            return this;
        }

        @JsonIgnore
        public Builder workspaces(String... workspaces) {
            return workspaces(asList(workspaces));
        }

        @JsonProperty("locales")
        public Builder locales(Collection<String> locales) {
            this.locales.addAll(locales);
            return this;
        }

        @JsonIgnore
        public Builder locales(String... locales) {
            return locales(asList(locales));
        }

        public Builder criterion(TranslationSearchCriterion criterion) {
            this.criterion = criterion;
            return this;
        }

        public Builder keyPattern(TranslationKeyPatternDto keyPattern) {
            this.keyPattern = keyPattern;
            return this;
        }

        public Builder maxKeys(Integer maxKeys) {
            if (maxKeys != null) {
                Assert.isTrue(maxKeys > 0, "The max number of keys must be greater than 0, but was " + maxKeys + ".");
                Assert.isTrue(maxKeys <= MAX_ALLOWED_KEYS,
                        "The maximum number of keys must be lower than " + MAX_ALLOWED_KEYS + ", but was " + maxKeys + ".");
            }

            this.maxKeys = maxKeys;

            return this;
        }

        public Builder pageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
            return this;
        }

        public TranslationsSearchRequestDto build() {
            return new TranslationsSearchRequestDto(this);
        }
    }

}
