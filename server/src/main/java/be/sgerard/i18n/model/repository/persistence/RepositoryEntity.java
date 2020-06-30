package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.RepositoryType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Repository that can be of different type. A repository contains translations.
 *
 * @author Sebastien Gerard
 */
@Document("repository")
public abstract class RepositoryEntity {

    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    private RepositoryStatus status = RepositoryStatus.NOT_INITIALIZED;

    private TranslationsConfigurationEntity translationsConfiguration;

    RepositoryEntity() {
    }

    public RepositoryEntity(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.translationsConfiguration = new TranslationsConfigurationEntity();
    }

    /**
     * Returns the {@link RepositoryType type} of this repository.
     */
    @Field(name = "type")
    public abstract RepositoryType getType();

    /**
     * Returns the unique id of this repository.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this repository.
     */
    public RepositoryEntity setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the display name to use for this repository.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the display name to use for this repository.
     */
    public RepositoryEntity setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns the {@link RepositoryStatus current status}.
     */
    public RepositoryStatus getStatus() {
        return status;
    }

    /**
     * Sets the {@link RepositoryStatus current status}.
     */
    public RepositoryEntity setStatus(RepositoryStatus status) {
        this.status = status;
        return this;
    }

    /**
     * Returns the {@link TranslationsConfigurationEntity configuration} to use for managing translations.
     */
    public TranslationsConfigurationEntity getTranslationsConfiguration() {
        return translationsConfiguration;
    }

    /**
     * Sets the {@link TranslationsConfigurationEntity configuration} to use for managing translations.
     */
    public RepositoryEntity setTranslationsConfiguration(TranslationsConfigurationEntity translationsConfiguration) {
        this.translationsConfiguration = translationsConfiguration;
        return this;
    }

}
