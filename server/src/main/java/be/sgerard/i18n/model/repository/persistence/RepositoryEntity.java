package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.RepositoryType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
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
@Getter
@Setter
@Accessors(chain = true)
public abstract class RepositoryEntity {

    /**
     * The unique id of this repository.
     */
    @Id
    private String id;

    /**
     * The display name to use for this repository.
     */
    @NotNull
    private String name;

    /**
     * The {@link RepositoryStatus current status}.
     */
    @NotNull
    private RepositoryStatus status = RepositoryStatus.NOT_INITIALIZED;

    /**
     * Flag indicating whether repository's workspaces must be automatically synchronized.
     */
    @NotNull
    private boolean autoSynchronized;

    /**
     * The {@link TranslationsConfigurationEntity configuration} to use for managing translations.
     */
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
     * Returns whether the specified branch is the default one.
     */
    public abstract boolean isDefaultBranch(String branch);

}
