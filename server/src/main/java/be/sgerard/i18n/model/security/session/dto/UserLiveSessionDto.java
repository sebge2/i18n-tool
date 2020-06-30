package be.sgerard.i18n.model.security.session.dto;

import be.sgerard.i18n.model.security.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description of a user live session.
 *
 * @author Sebastien Gerard
 */
@ApiModel(description = "Description of a user live session.")
@JsonDeserialize(builder = UserLiveSessionDto.Builder.class)
public class UserLiveSessionDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(UserLiveSessionEntity userSessionEntity) {
        return builder()
                .id(userSessionEntity.getId())
                .user(UserDto.builder(userSessionEntity.getUser()).build());
    }

    @ApiModelProperty(notes = "Id of this session.", required = true)
    private final String id;

    @ApiModelProperty(notes = "User associated to this session.", required = true)
    private final UserDto user;

    private UserLiveSessionDto(Builder builder) {
        id = builder.id;
        user = builder.user;
    }

    /**
     * Returns the unique id of this live session.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the associated {@link UserDto user}.
     */
    public UserDto getUser() {
        return user;
    }

    /**
     * Builder of a {@link UserLiveSessionDto user live session}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String id;
        private UserDto user;

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

        public UserLiveSessionDto build() {
            return new UserLiveSessionDto(this);
        }
    }
}
