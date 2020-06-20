package be.sgerard.i18n.model.security.session;

import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.service.security.UserRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "user_live_session")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"simpSessionId"})
        }
)
public class UserLiveSessionEntity {

    @Id
    private String id;

    @NotNull
    @Column(nullable = false)
    private String authenticatedUserId;

    @NotNull
    @ManyToOne(optional = false)
    private UserEntity user;

    @NotNull
    @Column(nullable = false)
    private String simpSessionId;

    @NotNull
    @Column(nullable = false)
    private Instant loginTime;

    @Column
    private Instant logoutTime;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<UserRole> sessionRoles = new ArrayList<>();

    @Version
    private int version;

    UserLiveSessionEntity() {
    }

    public UserLiveSessionEntity(UserEntity user,
                                 String authenticatedUserId,
                                 String simpSessionId,
                                 Instant loginTime,
                                 Collection<UserRole> sessionRoles) {
        this.authenticatedUserId = authenticatedUserId;
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.simpSessionId = simpSessionId;
        this.loginTime = loginTime;
        this.sessionRoles.addAll(sessionRoles);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthenticatedUserId() {
        return authenticatedUserId;
    }

    public void setAuthenticatedUserId(String authenticatedUserId) {
        this.authenticatedUserId = authenticatedUserId;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getSimpSessionId() {
        return simpSessionId;
    }

    public void setSimpSessionId(String simpSessionId) {
        this.simpSessionId = simpSessionId;
    }

    public Instant getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Instant loginTime) {
        this.loginTime = loginTime;
    }

    public Instant getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Instant logoutTime) {
        this.logoutTime = logoutTime;
    }

    public List<UserRole> getSessionRoles() {
        return sessionRoles;
    }

    public void setSessionRoles(Collection<UserRole> sessionRoles) {
        this.sessionRoles = new ArrayList<>(sessionRoles);
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
