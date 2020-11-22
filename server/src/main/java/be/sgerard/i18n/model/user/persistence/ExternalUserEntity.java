package be.sgerard.i18n.model.user.persistence;

import be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * External {@link UserEntity user}.
 *
 * @author Sebastien Gerard
 */
@Document("user")
@TypeAlias("external")
@Getter
@Setter
@Accessors(chain = true)
public class ExternalUserEntity extends UserEntity {

    /**
     * The unique id of the user in the external system.
     */
    @NotNull
    @Indexed
    private String externalId;

    /**
     * The {@link ExternalAuthSystem system} that authenticated the user.
     */
    @NotNull
    private ExternalAuthSystem externalAuthSystem;

    /**
     * The user's avatar URL to be displayed to the end-user.
     */
    private String avatarUrl;

    @PersistenceConstructor
    ExternalUserEntity() {
    }

    public ExternalUserEntity(String externalId, ExternalAuthSystem externalAuthSystem) {
        setId(UUID.randomUUID().toString());
        setPreferences(new UserPreferencesEntity());

        this.externalId = externalId;
        this.externalAuthSystem = externalAuthSystem;
    }
}
