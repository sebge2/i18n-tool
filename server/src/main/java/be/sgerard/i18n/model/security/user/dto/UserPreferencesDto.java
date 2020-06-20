package be.sgerard.i18n.model.security.user.dto;

import be.sgerard.i18n.model.ToolLocale;
import be.sgerard.i18n.model.security.user.persistence.UserPreferencesEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Optional;

/**
 * Preferences of a user.
 *
 * @author Sebastien Gerard
 */
@ApiModel(value = "UserPreferences", description = "Preferences of a user.")
@JsonDeserialize(builder = UserPreferencesDto.Builder.class)
public class UserPreferencesDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UserPreferencesEntity preferencesEntity) {
        return builder()
                .toolLocale(preferencesEntity.getToolLocale().orElse(null));
    }

    @ApiModelProperty(notes = "The locale to use for the user.")
    private final ToolLocale toolLocale;

    private UserPreferencesDto(Builder builder) {
        toolLocale = builder.toolLocale;
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

        private Builder() {
        }

        public Builder toolLocale(ToolLocale toolLocale) {
            this.toolLocale = toolLocale;
            return this;
        }

        public UserPreferencesDto build() {
            return new UserPreferencesDto(this);
        }
    }
}
