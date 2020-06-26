package be.sgerard.i18n.model.security.session.persistence;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private String userId;

    @NotNull
    @Indexed
    private String simpSessionId;

    @NotNull
    private Instant loginTime;

    private Instant logoutTime;

    private final List<UserRole> sessionRoles = new ArrayList<>();

    @PersistenceConstructor
    UserLiveSessionEntity() {
    }

    public UserLiveSessionEntity(UserEntity userId,
                                 String authenticatedUserId,
                                 String simpSessionId,
                                 Instant loginTime,
                                 Collection<UserRole> sessionRoles) {
        this.authenticatedUserId = authenticatedUserId;
        this.id = UUID.randomUUID().toString();
        this.userId = userId.getId();
        this.simpSessionId = simpSessionId;
        this.loginTime = loginTime;
        this.sessionRoles.addAll(sessionRoles);
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
     * Returns the SIMP unique session id.
     */
    public String getSimpSessionId() {
        return simpSessionId;
    }

    /**
     * Sets the SIMP unique session id.
     */
    public void setSimpSessionId(String simpSessionId) {
        this.simpSessionId = simpSessionId;
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
    public List<UserRole> getSessionRoles() {
        return sessionRoles;
    }

    /**
     * Adds all the {@link UserRole roles} that are associated to the user.
     */
    public void addSessionRoles(Collection<UserRole> sessionRoles) {
        this.sessionRoles.addAll(sessionRoles);
    }
}
