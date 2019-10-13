package be.sgerard.i18n.model.security.user;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * @author Sebastien Gerard
 */
@Entity(name = "user")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"externalId"})
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class UserEntity {

    @Id
    private String id;

    @NotNull
    @Column(nullable = false)
    private String username;

    @NotNull
    @Column(nullable = false)
    private String email;

    @Column
    private String avatarUrl;

    // TODO connection

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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
