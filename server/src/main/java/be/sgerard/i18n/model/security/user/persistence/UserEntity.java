package be.sgerard.i18n.model.security.user.persistence;

import be.sgerard.i18n.service.security.UserRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User allowed to access the application.
 *
 * @author Sebastien Gerard
 */
@Document("user")
public abstract class UserEntity {

    @Id
    private String id;

    @NotNull
    @Indexed
    private String username;

    private String email;

    private String avatarUrl;

    private List<UserRole> roles = new ArrayList<>();

    private UserPreferencesEntity preferences;

    protected UserEntity() {
    }

    /**
     * Returns the unique id of this user.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this user.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the username that will be displayed to the end-user.
     * <p>
     * For internal users, the username is used to log in.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username that will be displayed to the end-user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the user's avatar URL to be displayed to the end-user.
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Sets the user's avatar URL to be displayed to the end-user.
     */
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    /**
     * Returns {@link UserRole#isAssignableByEndUser() assigned} {@link UserRole roles}.
     */
    public List<UserRole> getRoles() {
        return roles;
    }

    /**
     * Returns the user's avatar URL to be displayed to the end-user.
     */
    public void setRoles(Collection<UserRole> roles) {
        this.roles = new ArrayList<>(roles);
    }

    /**
     * Returns {@link UserPreferencesEntity user's preferences}.
     */
    public UserPreferencesEntity getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferencesEntity preferences) {
        this.preferences = preferences;
    }

}
