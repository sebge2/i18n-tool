package be.sgerard.i18n.model.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

/**
 * Update of the current authenticated user.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "CurrentUserPatch", description = "The update of the current user.")
@JsonDeserialize(builder = CurrentUserPatchDto.Builder.class)
public class CurrentUserPatchDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "The new username.")
    private final String username;

    @Schema(description = "Name to display for this user (typically: first and last name).")
    private final String displayName;

    @Schema(description = "The new email address.")
    private final String email;

    private CurrentUserPatchDto(Builder builder) {
        username = builder.username;
        displayName = builder.displayName;
        email = builder.email;
    }

    /**
     * Returns the new username.
     */
    public Optional<String> getUsername() {
        return Optional.ofNullable(username);
    }

    /**
     * Returns the name to display for this user.
     */
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(displayName);
    }

    /**
     * Returns the new email address.
     */
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    /**
     * Builder of {@link CurrentUserPatchDto user patch}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String username;
        private String displayName;
        private String email;

        private Builder() {
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public CurrentUserPatchDto build() {
            return new CurrentUserPatchDto(this);
        }
    }
}
