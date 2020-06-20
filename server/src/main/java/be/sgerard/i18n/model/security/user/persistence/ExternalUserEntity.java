package be.sgerard.i18n.model.security.user.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
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

    ExternalUserEntity() {
    }

    public ExternalUserEntity(String externalId) {
        setId(UUID.randomUUID().toString());
        setPreferences(new UserPreferencesEntity(this));

        this.externalId = externalId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }
}
