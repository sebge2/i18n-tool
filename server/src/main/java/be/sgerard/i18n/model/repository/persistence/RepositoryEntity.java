package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.RepositoryType;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static java.util.Collections.unmodifiableCollection;

/**
 * Repository that can be of different type. A repository contains translations.
 *
 * @author Sebastien Gerard
 */
@Entity(name = "repository")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class RepositoryEntity {

    @Id
    private String id;

    @NotNull
    @Column(nullable = false, length = 1000)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RepositoryStatus status = RepositoryStatus.NOT_INITIALIZED;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private final Collection<WorkspaceEntity> workspaces = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "repository")
    private TranslationsConfigurationEntity translationsConfiguration;

    @Version
    private int version;

    RepositoryEntity() {
    }

    public RepositoryEntity(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.translationsConfiguration = new TranslationsConfigurationEntity(this);
    }

    /**
     * Returns the {@link RepositoryType type} of this repository.
     */
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
     * Returns all the {@link WorkspaceEntity workspaces} based on this repository.
     */
    public Collection<WorkspaceEntity> getWorkspaces() {
        return unmodifiableCollection(workspaces);
    }

    /**
     * Adds a {@link WorkspaceEntity workspace} to this repository.
     */
    public void addWorkspace(WorkspaceEntity workspace) {
        this.workspaces.add(workspace);
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

    /**
     * Creates a deep copy of this entity.
     */
    public abstract RepositoryEntity deepCopy();

    /**
     * Fills the specified entity with the state of this one.
     */
    protected <R extends RepositoryEntity> R fillEntity(R copy) {
        ((RepositoryEntity) copy).id = this.id;
        ((RepositoryEntity) copy).name = this.name;
        ((RepositoryEntity) copy).status = this.status;
        ((RepositoryEntity) copy).translationsConfiguration = this.translationsConfiguration.deepCopy(copy);
        ((RepositoryEntity) copy).version = this.version;

        return copy;
    }
}
