package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * Request asking the listing of paginated translations.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "TranslationsSearchRequest", description = "Request asking the listing of paginated translations.")
@JsonDeserialize(builder = TranslationsSearchRequestDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
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

    /**
     * The {@link WorkspaceEntity#getId() ids} of workspaces to search inside.
     */
    @Schema(description = "Search translations only in those workspaces", required = true)
    @Singular
    private final List<String> workspaces;

    /**
     * The {@link TranslationLocaleEntity#getId() ids} of locales of translations to look for.
     */
    @Schema(description = "Search translations only in those translations locale ids.")
    @Singular
    private final List<String> locales;

    /**
     * {@link BundleFileEntity Bundle files} of translations to look for.
     */
    @Schema(description = "Search translations only in those bundle file ids.")
    @Singular
    private final List<String> bundleFiles;

    /**
     * The {@link TranslationSearchCriterion criterion} that translations must have.
     */
    @Schema(description = "Specify the criterion that translations must have.")
    private final TranslationSearchCriterion criterion;

    /**
     * The {@link TranslationKeyPatternDto pattern} to use of keys to retrieve.
     */
    @Schema(description = "The pattern to use of keys to retrieve.")
    private final TranslationKeyPatternDto keyPattern;

    /**
     * The {@link TranslationValueRestrictionDto restriction} to apply on translation values.
     */
    @Schema(description = "The restriction to apply on translation values.")
    private final TranslationValueRestrictionDto valueRestriction;

    /**
     * The maximum number of keys for the next page.
     */
    @Schema(description = "The maximum number of keys for the next page.")
    private final int maxKeys;

    /**
     * The last element of the previous page (used to identify the following page).
     */
    @Schema(description = "The last element of the previous page, used to get the next page.")
    private final String lastPageKey;

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
     * @see #lastPageKey
     */
    public Optional<String> getLastPageKey() {
        return Optional.ofNullable(lastPageKey);
    }

    /**
     * Builder of {@link TranslationsSearchRequestDto translations search request}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        @SuppressWarnings({"FieldCanBeLocal", "unused", "FieldMayBeFinal"})
        private TranslationSearchCriterion criterion = TranslationSearchCriterion.ALL;

        @SuppressWarnings({"FieldCanBeLocal", "unused"})
        private int maxKeys = DEFAULT_MAX_KEYS;

        public Builder maxKeys(int maxKeys) {
            Assert.isTrue(maxKeys > 0, "The max number of keys must be greater than 0, but was " + maxKeys + ".");
            Assert.isTrue(maxKeys <= MAX_ALLOWED_KEYS,
                    "The maximum number of keys must be lower than " + MAX_ALLOWED_KEYS + ", but was " + maxKeys + ".");

            this.maxKeys = maxKeys;

            return this;
        }
    }

}
