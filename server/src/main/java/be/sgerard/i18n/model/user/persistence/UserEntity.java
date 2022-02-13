package be.sgerard.i18n.model.user.persistence;

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
@Document(UserEntity.USER_DOCUMENT)
@Getter
@Setter
@Accessors(chain = true)
public abstract class UserEntity {

    /**
     * User name of admin.
     */
    public static final String ADMIN_USER_NAME = "admin";

    /**
     * Document name for this entity.
     */
    public static final String USER_DOCUMENT = "user";

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

    /**
     * Updates all roles that are {@link UserRole#isAssignableByEndUser() assignables} and preserve the other ones.
     */
    public UserEntity updateAssignableRoles(Collection<UserRole> roles) {
        this.roles.stream().filter(UserRole::isAssignableByEndUser).forEach(this.roles::remove);
        this.roles.addAll(roles);
        return this;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "id='" + id + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
