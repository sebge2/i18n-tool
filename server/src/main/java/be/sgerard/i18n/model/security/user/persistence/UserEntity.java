package be.sgerard.i18n.model.security.user.persistence;

import be.sgerard.i18n.service.security.UserRole;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserEntity {

    @Id
    private String id;

    @NotNull
    @Column(nullable = false)
    private String username;

    @Column
    private String email;

    @Column
    private String avatarUrl;

    @ElementCollection(fetch = FetchType.LAZY)
    @Enumerated(EnumType.STRING)
    private List<UserRole> roles = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private UserPreferencesEntity preferences;

    @Version
    private int version;

    UserEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(Collection<UserRole> roles) {
        this.roles = new ArrayList<>(roles);
    }

    public UserPreferencesEntity getPreferences() {
        return preferences;
    }

    public void setPreferences(UserPreferencesEntity preferences) {
        this.preferences = preferences;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
