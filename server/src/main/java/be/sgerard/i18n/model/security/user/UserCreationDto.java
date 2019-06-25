package be.sgerard.i18n.model.security.user;

import be.sgerard.i18n.service.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "The initial information needed to create an internal user.")
@JsonDeserialize(builder = UserCreationDto.Builder.class)
public class UserCreationDto {

    public static Builder builder() {
        return new Builder();
    }

    private final String username;
    private final String email;
    private final String password;
    private final String avatarUrl;
    private final Collection<UserRole> roles;

    private UserCreationDto(Builder builder) {
        roles = builder.roles;
        username = builder.username;
        email = builder.email;
        password = builder.password;
        avatarUrl = builder.avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Collection<UserRole> getRoles() {
        return roles;
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String username;
        private String email;
        private String password;
        private String avatarUrl;
        private final Collection<UserRole> roles = new HashSet<>();

        private Builder() {
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        @JsonProperty("roles")
        public Builder roles(Collection<UserRole> roles) {
            this.roles.addAll(roles);
            return this;
        }

        @JsonIgnore
        public Builder roles(UserRole... roles) {
            return roles(asList(roles));
        }

        public UserCreationDto build() {
            return new UserCreationDto(this);
        }
    }
}
