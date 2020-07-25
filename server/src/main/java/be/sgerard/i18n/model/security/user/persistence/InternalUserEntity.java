package be.sgerard.i18n.model.security.user.persistence;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Internal {@link UserEntity user}.
 *
 * @author Sebastien Gerard
 */
@Document("user")
@TypeAlias("internal")
@Getter
@Setter
@Accessors(chain = true)
public class InternalUserEntity extends UserEntity {

    /**
     * The encoded password.
     */
    @NotNull
    private String password;

    /**
     * The avatar image (PNG image).
     */
    @NotNull
    private byte[] avatar;

    @PersistenceConstructor
    InternalUserEntity() {
    }

    public InternalUserEntity(String username) {
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setPreferences(new UserPreferencesEntity());
    }

    /**
     * Returns whether an avatar is available.
     */
    public boolean hasAvatar(){
        return (getAvatar() != null) && (getAvatar().length > 0);
    }
}
