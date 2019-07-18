package be.sgerard.poc.githuboauth.model.security.session;

import be.sgerard.poc.githuboauth.model.security.user.UserDto;

import java.time.Instant;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
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

    private final String id;
    private final UserDto user;
    private final String simpSessionId;
    private final Instant loginTime;
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
