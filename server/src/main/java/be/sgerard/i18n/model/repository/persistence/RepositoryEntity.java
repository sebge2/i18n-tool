package be.sgerard.i18n.model.repository.persistence;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.RepositoryType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.UUID;

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

    @Version
    private int version;

    RepositoryEntity() {
    }

    public RepositoryEntity(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
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
     * Returns the editing version of this entity.
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the editing version of this entity.
     */
    public RepositoryEntity setVersion(int version) {
        this.version = version;
        return this;
    }

    /**
     * Creates a deep copy of this entity.
     */
    public abstract RepositoryEntity deepCopy();

    /**
     * Fills the specified entity with the state of this one.
     */
    protected void fillEntity(RepositoryEntity copy) {
        copy.id = this.id;
        copy.name = this.name;
        copy.status = this.status;
        copy.version = this.version;
    }
}
