package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.security.user.persistence.UserPreferencesEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

/**
 * Preferences of a user.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "UserPreferences", description = "Preferences of a user.")
@JsonDeserialize(builder = UserPreferencesDto.Builder.class)
@Getter
public class UserPreferencesDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UserPreferencesEntity preferencesEntity) {
        return builder()
                .toolLocale(preferencesEntity.getToolLocale().orElse(null))
                .preferredLocales(preferencesEntity.getPreferredLocales().stream().map(TranslationLocaleEntity::getId).collect(toList()));
    }

    @Schema(description = "The locale to use for the user.")
    private final ToolLocale toolLocale;

    @Schema(description = "Locales that are preferred/spoken by the end-user.")
    private final List<String> preferredLocales;

    private UserPreferencesDto(Builder builder) {
        toolLocale = builder.toolLocale;
        preferredLocales = unmodifiableList(builder.preferredLocales);
    }

    /**
     * Returns the {@link ToolLocale locale} to use for this user.
     */
    public Optional<ToolLocale> getToolLocale() {
        return Optional.ofNullable(toolLocale);
    }

    /**
     * Builder of {@link UserPreferencesDto user preferences}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private ToolLocale toolLocale;
        private final List<String> preferredLocales = new ArrayList<>();

        private Builder() {
        }

        public Builder toolLocale(ToolLocale toolLocale) {
            this.toolLocale = toolLocale;
            return this;
        }

        @JsonProperty("preferredLocales")
        public Builder preferredLocales(List<String> preferredLocales) {
            this.preferredLocales.addAll(preferredLocales);
            return this;
        }

        @JsonIgnore
        public Builder preferredLocales(String... preferredLocales) {
            return preferredLocales(asList(preferredLocales));
        }

        public UserPreferencesDto build() {
            return new UserPreferencesDto(this);
        }
    }
}
