package be.sgerard.poc.githuboauth.model.security.session;

import be.sgerard.poc.githuboauth.model.security.user.UserEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "user_session")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"simpSessionId"})
        }
)
public class UserSessionEntity {

    @Id
    private String id;

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

    @Version
    private int version;

    UserSessionEntity() {
    }

    public UserSessionEntity(UserEntity user, String simpSessionId, Instant loginTime) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.simpSessionId = simpSessionId;
        this.loginTime = loginTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
