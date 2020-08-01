package be.sgerard.i18n.model.security.session.dto;

import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Description of a user live session.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "UserLiveSession", description = "Description of a user live session.")
@JsonDeserialize(builder = UserLiveSessionDto.Builder.class)
@Getter
public class UserLiveSessionDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UserLiveSessionEntity userLiveSession) {
        return builder()
                .id(userLiveSession.getId())
                .userId(userLiveSession.getUser().getId())
                .userDisplayName(userLiveSession.getUser().getDisplayName());
    }

    public static UserLiveSessionDto toDto(UserLiveSessionEntity userLiveSession) {
        return builder(userLiveSession).build();
    }

    @Schema(description = "Id of this session.", required = true)
    private final String id;

    @Schema(description = "Id of the User associated to this session.", required = true)
    private final String userId;

    @Schema(description = "Display name of the user associated to this session.", required = true)
    private final String userDisplayName;

    private UserLiveSessionDto(Builder builder) {
        id = builder.id;
        userId = builder.userId;
        userDisplayName = builder.userDisplayName;
    }

    /**
     * Builder of a {@link UserLiveSessionDto user live session}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String id;
        private String userId;
        private String userDisplayName;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder userDisplayName(String userDisplayName) {
            this.userDisplayName = userDisplayName;
            return this;
        }

        public UserLiveSessionDto build() {
            return new UserLiveSessionDto(this);
        }
    }
}
