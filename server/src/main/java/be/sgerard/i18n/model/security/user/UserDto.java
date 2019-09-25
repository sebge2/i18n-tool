package be.sgerard.i18n.model.security.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.unmodifiableSet;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "Description of the user.")
@JsonDeserialize(builder = UserDto.Builder.class)
public class UserDto implements Principal {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UserEntity userEntity) {
        return builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .avatarUrl(userEntity.getAvatarUrl());
    }

    @ApiModelProperty(notes = "Id of the user.")
    private final String id;

    @ApiModelProperty(notes = "Username of the user.")
    private final String username;

    @ApiModelProperty(notes = "Email of the user.")
    private final String email;

    @ApiModelProperty(notes = "URL of the user avatar.")
    private final String avatarUrl;

    @ApiModelProperty(notes = "User roles.")
    private final Collection<String> roles;

    private UserDto(Builder builder) {
        id = builder.id;
        username = builder.username;
        email = builder.email;
        avatarUrl = builder.avatarUrl;
        roles = unmodifiableSet(builder.roles);
    }

    @Override
    @JsonIgnore
    public String getName() {
        return getId();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        return "User(" + username + ", " + email + ')';
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String id;
        private String username;
        private String email;
        private String avatarUrl;
        private final Set<String> roles = new HashSet<>();

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder roles(Collection<String> roles) {
            this.roles.addAll(roles);
            return this;
        }

        public UserDto build() {
            return new UserDto(this);
        }
    }
}
