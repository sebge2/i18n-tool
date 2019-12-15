package be.sgerard.i18n.model.security.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "internal_user")
public class InternalUserEntity extends UserEntity {

    @NotNull
    @Column(nullable = false)
    private String password;

    InternalUserEntity() {
    }

    public InternalUserEntity(String username) {
        setId(UUID.randomUUID().toString());
        setUsername(username);
        setPreferences(new UserPreferencesEntity(this));
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
