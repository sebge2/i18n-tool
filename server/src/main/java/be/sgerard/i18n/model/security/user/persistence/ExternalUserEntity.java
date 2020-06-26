package be.sgerard.i18n.model.security.user.persistence;

import be.sgerard.i18n.model.security.user.ExternalAuthSystem;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * External {@link UserEntity user}.
 *
 * @author Sebastien Gerard
 */
@Entity(name = "external_user")
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"externalId"})
        }
)
public class ExternalUserEntity extends UserEntity {

    @NotNull
    @Column(nullable = false)
    private String externalId;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExternalAuthSystem externalAuthSystem;

    @PersistenceConstructor
    ExternalUserEntity() {
    }

    public ExternalUserEntity(String externalId, ExternalAuthSystem externalAuthSystem) {
        setId(UUID.randomUUID().toString());
        setPreferences(new UserPreferencesEntity());

        this.externalId = externalId;
        this.externalAuthSystem = externalAuthSystem;
    }

    /**
     * Returns the unique id of the user in the external system.
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Sets the unique id of the user in the external system.
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * Returns the {@link ExternalAuthSystem system} that authenticated the user.
     */
    public ExternalAuthSystem getExternalAuthSystem() {
        return externalAuthSystem;
    }

    /**
     * Sets the {@link ExternalAuthSystem system} that authenticated the user.
     */
    public ExternalUserEntity setExternalAuthSystem(ExternalAuthSystem externalAuthSystem) {
        this.externalAuthSystem = externalAuthSystem;
        return this;
    }
}
