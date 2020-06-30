package be.sgerard.i18n.model.security.session.persistence;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * Description of a user live session.
 *
 * @author Sebastien Gerard
 */
@Document("user_live_session")
public class UserLiveSessionEntity {

    @Id
    private String id;

    @NotNull
    private String authenticatedUserId;

    @NotNull
    private String userId; // TODO

    @NotNull
    private Instant loginTime;

    private Instant logoutTime;

    private Collection<UserRole> sessionRoles = new ArrayList<>();

    @PersistenceConstructor
    UserLiveSessionEntity() {
    }

    public UserLiveSessionEntity(AuthenticatedUser authenticatedUser) {
        this.authenticatedUserId = authenticatedUser.getId();
        this.id = UUID.randomUUID().toString();
        this.userId = authenticatedUser.getUser().getId();
        this.loginTime = Instant.now();
        this.sessionRoles.addAll(authenticatedUser.getSessionRoles());
    }

    /**
     * Returns the unique id of this live session.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this live session.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the {@link AuthenticatedUser#getId() id} of the authenticated user.
     */
    public String getAuthenticatedUserId() {
        return authenticatedUserId;
    }

    /**
     * Sets the {@link AuthenticatedUser#getId() id} of the authenticated user.
     */
    public void setAuthenticatedUserId(String authenticatedUserId) {
        this.authenticatedUserId = authenticatedUserId;
    }

    /**
     * Returns the associated {@link UserEntity user}.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the associated {@link UserEntity user}.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the time when the user logged in.
     */
    public Instant getLoginTime() {
        return loginTime;
    }

    /**
     * Sets the time when the user logged in.
     */
    public void setLoginTime(Instant loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * Returns the time when the live session ends.
     */
    public Instant getLogoutTime() {
        return logoutTime;
    }

    /**
     * Sets the time when the live session ends.
     */
    public void setLogoutTime(Instant logoutTime) {
        this.logoutTime = logoutTime;
    }

    /**
     * Returns all the {@link UserRole roles} that are associated to the user.
     */
    public Collection<UserRole> getSessionRoles() {
        return sessionRoles;
    }

    /**
     * Adds all the {@link UserRole roles} that are associated to the user.
     */
    public void setSessionRoles(Collection<UserRole> sessionRoles) {
        this.sessionRoles = sessionRoles;
    }
}
