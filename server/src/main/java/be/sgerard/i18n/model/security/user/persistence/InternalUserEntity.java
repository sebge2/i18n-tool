package be.sgerard.i18n.model.security.user.persistence;

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
public class InternalUserEntity extends UserEntity {

    @NotNull
    private String password;

    @PersistenceConstructor
    InternalUserEntity() {
    }

    public InternalUserEntity(String username) {
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setPreferences(new UserPreferencesEntity());
    }

    /**
     * Returns the encoded password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the encoded password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
