package be.sgerard.poc.githuboauth.model.i18n.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

import static java.util.Collections.unmodifiableCollection;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "Request asking the listing of paginated translations.")
public class BundleKeysPageRequestDto {

    public static final int DEFAULT_MAX_KEYS = 50;

    public static final int MAX_ALLOWED_KEYS = 500;

    public static Builder builder(String workspaceId) {
        return new Builder(workspaceId);
    }

    @ApiModelProperty(notes = "Unique identifier of the workspace containing translations.", required = true)
    private final String workspaceId;

    @ApiModelProperty(notes = "Search translations only in those locales.")
    private final Collection<Locale> locales;

    @ApiModelProperty(notes = "Specify the criterion that the translations must have.")
    private final TranslationSearchCriterion criterion;

    @ApiModelProperty(notes = "The pattern to use of the key to retrieve. Follow SQL like patterns.")
    private final String keyPattern;

    @ApiModelProperty(notes = "The maximum number of keys for the next page.")
    private final int maxKeys;

    @ApiModelProperty(notes = "The last key contained in the previous page (nothing if it's the first page).")
    private final String lastKey;

    private BundleKeysPageRequestDto(Builder builder) {
        workspaceId = builder.workspaceId;
        locales = unmodifiableCollection(builder.locales);
        criterion = (builder.criterion != null) ? builder.criterion : TranslationSearchCriterion.ALL;
        keyPattern = StringUtils.isEmpty(builder.keyPattern) ? null : builder.keyPattern;
        maxKeys = (builder.maxKeys != null) ? builder.maxKeys : DEFAULT_MAX_KEYS;
        lastKey = builder.lastKey;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public Collection<Locale> getLocales() {
        return locales;
    }

    public TranslationSearchCriterion getCriterion() {
        return criterion;
    }

    public Optional<String> getKeyPattern() {
        return Optional.ofNullable(keyPattern);
    }

    public int getMaxKeys() {
        return maxKeys;
    }

    public Optional<String> getLastKey() {
        return Optional.ofNullable(lastKey);
    }

    public static final class Builder {

        private final String workspaceId;
        private final Collection<Locale> locales = new HashSet<>();
        private TranslationSearchCriterion criterion;
        private String keyPattern;
        private Integer maxKeys;
        private String lastKey;

        private Builder(String workspaceId) {
            this.workspaceId = workspaceId;
        }

        public Builder locales(Collection<Locale> locales) {
            this.locales.addAll(locales);
            return this;
        }

        public Builder criterion(TranslationSearchCriterion criterion) {
            this.criterion = criterion;
            return this;
        }

        public Builder keyPattern(String keyPattern) {
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

        public BundleKeysPageRequestDto build() {
            return new BundleKeysPageRequestDto(this);
        }
    }

}
