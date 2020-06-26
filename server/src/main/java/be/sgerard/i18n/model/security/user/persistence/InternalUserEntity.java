package be.sgerard.i18n.model.security.user.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Internal {@link UserEntity user}.
 *
 * @author Sebastien Gerard
 */
@Entity(name = "internal_user")
public class InternalUserEntity extends UserEntity {

    @NotNull
    @Column(nullable = false)
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
