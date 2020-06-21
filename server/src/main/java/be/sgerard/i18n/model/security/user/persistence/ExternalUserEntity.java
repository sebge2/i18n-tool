package be.sgerard.i18n.model.security.user.persistence;

import javax.persistence.*;
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

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExternalAuthClient externalAuthClient;

    ExternalUserEntity() {
    }

    public ExternalUserEntity(String externalId, ExternalAuthClient externalAuthClient) {
        setId(UUID.randomUUID().toString());
        setPreferences(new UserPreferencesEntity(this));

        this.externalId = externalId;
        this.externalAuthClient = externalAuthClient;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public ExternalAuthClient getExternalAuthClient() {
        return externalAuthClient;
    }

    public ExternalUserEntity setExternalAuthClient(ExternalAuthClient externalAuthClient) {
        this.externalAuthClient = externalAuthClient;
        return this;
    }
}
