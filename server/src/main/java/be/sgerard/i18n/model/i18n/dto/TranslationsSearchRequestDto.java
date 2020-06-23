package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Collections.unmodifiableCollection;

/**
 * Request asking the listing of paginated translations.
 *
 * @author Sebastien Gerard
 */
@ApiModel(description = "Request asking the listing of paginated translations.")
@JsonPOJOBuilder(withPrefix = "")
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @ApiModelProperty(notes = "Search translations only in those workspaces", required = true)
    private final Collection<String> workspaces;

    @ApiModelProperty(notes = "Search translations only in those translations locale ids.")
    private final Collection<String> locales;

    @ApiModelProperty(notes = "Specify the criterion that translations must have.")
    private final TranslationSearchCriterion criterion;

    @ApiModelProperty(notes = "The pattern to use of keys to retrieve.")
    private final KeyPattern keyPattern;

    @ApiModelProperty(notes = "The maximum number of keys for the next page.")
    private final int maxKeys;

    @ApiModelProperty(notes = "The last key contained in the previous page (nothing if it's the first page).")
    private final String lastKey;

    private TranslationsSearchRequestDto(Builder builder) {
        workspaces = unmodifiableCollection(builder.workspaces);
        locales = unmodifiableCollection(builder.locales);
        criterion = (builder.criterion != null) ? builder.criterion : TranslationSearchCriterion.ALL;
        keyPattern = builder.keyPattern;
        maxKeys = (builder.maxKeys != null) ? builder.maxKeys : DEFAULT_MAX_KEYS;
        lastKey = builder.lastKey;
    }

    /**
     * Returns {@link WorkspaceEntity#getId() ids} of workspaces to search inside.
     */
    public Collection<String> getWorkspaces() {
        return workspaces;
    }

    /**
     * Returns {@link TranslationLocaleEntity#getId() ids} of translations to look for.
     */
    public Collection<String> getLocales() {
        return locales;
    }

    /**
     * Returns the criterion that translations must have.
     */
    public TranslationSearchCriterion getCriterion() {
        return criterion;
    }

    /**
     * Returns the pattern to use of keys to retrieve.
     */
    public Optional<KeyPattern> getKeyPattern() {
        return Optional.ofNullable(keyPattern);
    }

    /**
     * Returns the maximum number of keys for the next page.
     */
    public int getMaxKeys() {
        return maxKeys;
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
     * Builder of {@link TranslationsSearchRequestDto translations search request}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private final Collection<String> workspaces = new HashSet<>();
        private final Collection<String> locales = new HashSet<>();
        private TranslationSearchCriterion criterion;
        private KeyPattern keyPattern;
        private Integer maxKeys;
        private String lastKey;

        private Builder() {
        }

        public Builder workspaces(Collection<String> workspaces) {
            this.workspaces.addAll(workspaces);
            return this;
        }

        public Builder locales(Collection<String> locales) {
            this.locales.addAll(locales);
            return this;
        }

        public Builder criterion(TranslationSearchCriterion criterion) {
            this.criterion = criterion;
            return this;
        }

        public Builder keyPattern(KeyPattern keyPattern) {
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

        public Builder lastKey(String lastKey) {
            this.lastKey = lastKey;
            return this;
        }

        public TranslationsSearchRequestDto build() {
            return new TranslationsSearchRequestDto(this);
        }
    }

    /**
     * Pattern of a translation key to search for.
     */
    @ApiModel(description = "Pattern of a translation key to search for.")
    public static class KeyPattern {

        private final KeyPatternStrategy strategy;
        private final String pattern;

        @JsonCreator
        public KeyPattern(@JsonProperty("strategy") KeyPatternStrategy strategy,
                          @JsonProperty("pattern") String pattern) {
            this.strategy = strategy;
            this.pattern = pattern;
        }

        public KeyPatternStrategy getStrategy() {
            return strategy;
        }

        public String getPattern() {
            return pattern;
        }
    }

    /**
     * Strategy of the key pattern matching.
     */
    public enum KeyPatternStrategy {

        STARTS_WITH,

        ENDS_WITH,

        CONTAINS

    }
}
