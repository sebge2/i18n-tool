package be.sgerard.i18n.model.security.user.persistence;

import be.sgerard.i18n.service.security.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * User allowed to access the application.
 *
 * @author Sebastien Gerard
 */
@Document("user")
@Getter
@Setter
@Accessors(chain = true)
public abstract class UserEntity {

    /**
     * The unique id of this user.
     */
    @Id
    private String id;

    /**
     * The username that will be displayed to the end-user.
     * <p>
     * For internal users, the username is used to log in.
     */
    @NotNull
    @Indexed
    private String username;

    /**
     * The name to be displayed to the end-user (ideally composed of the first name, last name).
     */
    @NotNull
    private String displayName;

    /**
     * The user's email.
     */
    private String email;

    /**
     * The {@link UserRole#isAssignableByEndUser() assigned} {@link UserRole roles}.
     */
    private Set<UserRole> roles = new HashSet<>();

    /**
     * {@link UserPreferencesEntity User's preferences}.
     */
    private UserPreferencesEntity preferences;

    protected UserEntity() {
    }

    /**
     * @see #roles
     */
    public UserEntity setRoles(Collection<UserRole> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
        return this;
    }
}
