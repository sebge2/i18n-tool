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
    private final String keyPattern;
    private final Collection<Locale> missingLocales;
    private final Collection<Locale> locales;
    private final Boolean hasBeenUpdated;
    private final int maxKeyEntries;
    private final String lastKey;

    private BundleKeyEntrySearchRequestDto(Builder builder) {
        workspaceId = builder.workspaceId;
        keyPattern = StringUtils.isEmpty(builder.keyPattern) ? null : builder.keyPattern;
        missingLocales = unmodifiableCollection(builder.missingLocales);
        locales = unmodifiableCollection(builder.locales);
        hasBeenUpdated = builder.hasBeenUpdated;
        maxKeyEntries = (builder.maxKeyEntries != null) ? builder.maxKeyEntries : DEFAULT_MAX_KEYS;
        lastKey = builder.lastKey;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public Optional<String> getKeyPattern() {
        return Optional.ofNullable(keyPattern);
    }

    public Collection<Locale> getMissingLocales() {
        return missingLocales;
    }

    public Collection<Locale> getLocales() {
        return locales;
    }

    public Optional<Boolean> hasBeenUpdated() {
        return Optional.ofNullable(hasBeenUpdated);
    }

    public int getMaxKeyEntries() {
        return maxKeyEntries;
    }

    public Optional<String> getLastKey() {
        return Optional.ofNullable(lastKey);
    }

    public static final class Builder {

        private final String workspaceId;
        private String keyPattern;
        private final Collection<Locale> missingLocales = new HashSet<>();
        private final Collection<Locale> locales = new HashSet<>();
        private Boolean hasBeenUpdated;
        private Integer maxKeyEntries;
        private String lastKey;

        private Builder(String workspaceId) {
            this.workspaceId = workspaceId;
        }

        public Builder keyPattern(String keyPattern) {
            this.keyPattern = keyPattern;
            return this;
        }

        public Builder missingLocales(Collection<Locale> missingLocales) {
            this.missingLocales.addAll(missingLocales);
            return this;
        }

        public Builder locales(Collection<Locale> locales) {
            this.locales.addAll(locales);
            return this;
        }

        public Builder hasBeenUpdated(Boolean hasBeenUpdated) {
            this.hasBeenUpdated = hasBeenUpdated;
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
