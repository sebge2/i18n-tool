package be.sgerard.poc.githuboauth.model.security.session;

import be.sgerard.poc.githuboauth.model.security.user.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.Instant;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "Description of a user session.")
@JsonDeserialize(builder = UserSessionDto.Builder.class)
public class UserSessionDto {

    public static Builder userSessionDto() {
        return new Builder();
    }

    public static Builder userSessionDto(UserSessionEntity userSessionEntity) {
        return userSessionDto()
                .id(userSessionEntity.getId())
                .user(UserDto.builder(userSessionEntity.getUser()).build())
                .simpSessionId(userSessionEntity.getSimpSessionId())
                .loginTime(userSessionEntity.getLoginTime())
                .logoutTime(userSessionEntity.getLogoutTime());
    }

    @ApiModelProperty(notes = "Id of this session.", required = true)
    private final String id;

    @ApiModelProperty(notes = "User associated to this session.", required = true)
    private final UserDto user;

    @ApiModelProperty(notes = "Id of the SIMP session.", required = true)
    private final String simpSessionId;

    @ApiModelProperty(notes = "Time when this session was created.", required = true)
    private final Instant loginTime;

    @ApiModelProperty(notes = "Time when this session was closed.", dataType = "java.time.Instant")
    private final Instant logoutTime;

    private UserSessionDto(Builder builder) {
        id = builder.id;
        user = builder.user;
        simpSessionId = builder.simpSessionId;
        loginTime = builder.loginTime;
        logoutTime = builder.logoutTime;
    }

    public String getId() {
        return id;
    }

    public UserDto getUser() {
        return user;
    }

    public String getSimpSessionId() {
        return simpSessionId;
    }

    public Instant getLoginTime() {
        return loginTime;
    }

    public Optional<Instant> getLogoutTime() {
        return Optional.ofNullable(logoutTime);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String id;
        private UserDto user;
        private String simpSessionId;
        private Instant loginTime;
        private Instant logoutTime;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder user(UserDto user) {
            this.user = user;
            return this;
        }

        public Builder simpSessionId(String simpSessionId) {
            this.simpSessionId = simpSessionId;
            return this;
        }

        public Builder loginTime(Instant loginTime) {
            this.loginTime = loginTime;
            return this;
        }

        public Builder logoutTime(Instant logoutTime) {
            this.logoutTime = logoutTime;
            return this;
        }

        public UserSessionDto build() {
            return new UserSessionDto(this);
        }
    }
}
