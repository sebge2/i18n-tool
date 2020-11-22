package be.sgerard.i18n.model.user.snapshot;

import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import java.util.Optional;

/**
 * Snapshot of an {@link InternalUserEntity internal user}.
 *
 * @author Sebastien Gerard
 */
@Getter
@JsonDeserialize(builder = InternalUserSnapshotDto.Builder.class)
public class InternalUserSnapshotDto extends UserSnapshotDto {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * @see InternalUserEntity#getPassword()
     */
    private final String password;

    /**
     * @see InternalUserEntity#getAvatar()
     * <p>
     * Base-64 encoded.
     */
    private final String avatar;

    private InternalUserSnapshotDto(Builder builder) {
        super(builder);

        password = builder.password;
        avatar = builder.avatar;
    }

    @Override
    public Type getType() {
        return Type.INTERNAL;
    }

    /**
     * @see #avatar
     */
    public Optional<String> getAvatar() {
        return Optional.ofNullable(avatar);
    }

    /**
     * Builder of {@link InternalUserSnapshotDto user}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder extends UserSnapshotDto.Builder {

        private String password;
        private String avatar;

        private Builder() {
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        @Override
        public InternalUserSnapshotDto build() {
            return new InternalUserSnapshotDto(this);
        }
    }
}
