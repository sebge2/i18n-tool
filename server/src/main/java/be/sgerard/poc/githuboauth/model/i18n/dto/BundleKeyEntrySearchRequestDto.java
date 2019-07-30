package be.sgerard.poc.githuboauth.model.i18n.dto;

import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

import static java.util.Collections.unmodifiableCollection;

/**
 * @author Sebastien Gerard
 */
public class BundleKeyEntrySearchRequestDto {

    public static final int DEFAULT_MAX_KEYS = 50;

    public static Builder builder(String workspaceId) {
        return new Builder(workspaceId);
    }

    private final String workspaceId;
    private final Collection<Locale> locales;
    private final TranslationSearchCriterion criterion;
    private final String keyPattern;
    private final int maxKeyEntries;
    private final String lastKey;

    private BundleKeyEntrySearchRequestDto(Builder builder) {
        workspaceId = builder.workspaceId;
        locales = unmodifiableCollection(builder.locales);
        criterion = builder.criterion;
        keyPattern = StringUtils.isEmpty(builder.keyPattern) ? null : builder.keyPattern;
        maxKeyEntries = (builder.maxKeyEntries != null) ? builder.maxKeyEntries : DEFAULT_MAX_KEYS;
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

    public int getMaxKeyEntries() {
        return maxKeyEntries;
    }

    public Optional<String> getLastKey() {
        return Optional.ofNullable(lastKey);
    }

    public static final class Builder {

        private final String workspaceId;
        private final Collection<Locale> locales = new HashSet<>();
        private TranslationSearchCriterion criterion;
        private String keyPattern;
        private Integer maxKeyEntries;
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

        public Builder maxKeyEntries(Integer maxKeyEntries) {
            this.maxKeyEntries = maxKeyEntries;

            return this;
        }

        public Builder lastKey(String lastKey) {
            this.lastKey = lastKey;
            return this;
        }

        public BundleKeyEntrySearchRequestDto build() {
            return new BundleKeyEntrySearchRequestDto(this);
        }
    }
}
