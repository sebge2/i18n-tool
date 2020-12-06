package be.sgerard.i18n.model.user.snapshot;

import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

/**
 * Snapshot of an {@link ExternalUserEntity external user}.
 *
 * @author Sebastien Gerard
 */
@Getter
@JsonDeserialize(builder = ExternalUserSnapshotDto.Builder.class)
public class ExternalUserSnapshotDto extends UserSnapshotDto {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * @see ExternalUserEntity#getExternalId()
     */
    private final String externalId;

    /**
     * @see ExternalUserEntity#getExternalAuthSystem()
     */
    private final ExternalAuthSystem externalAuthSystem;

    /**
     * @see ExternalUserEntity#getAvatarUrl()
     */
    private final String avatarUrl;

    private ExternalUserSnapshotDto(Builder builder) {
        super(builder);

        externalId = builder.externalId;
        externalAuthSystem = builder.externalAuthSystem;
        avatarUrl = builder.avatarUrl;
    }

    @Override
    public Type getType() {
        return Type.EXTERNAL;
    }

    /**
     * Builder of {@link ExternalUserSnapshotDto user}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder extends UserSnapshotDto.Builder {

        private String externalId;
        private ExternalAuthSystem externalAuthSystem;
        private String avatarUrl;

        private Builder() {
        }

        public Builder externalId(String externalId) {
            this.externalId = externalId;
            return this;
        }

        public Builder externalAuthSystem(ExternalAuthSystem externalAuthSystem) {
            this.externalAuthSystem = externalAuthSystem;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        @Override
        public ExternalUserSnapshotDto build() {
            return new ExternalUserSnapshotDto(this);
        }
    }

    /**
     * @see be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem
     */
    public enum ExternalAuthSystem {

        /**
         * @see be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem#OAUTH_GOOGLE
         */
        OAUTH_GOOGLE,

        /**
         * @see be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem#OAUTH_GITHUB
         */
        OAUTH_GITHUB
    }
}
