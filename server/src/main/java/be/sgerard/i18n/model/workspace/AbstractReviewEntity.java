package be.sgerard.i18n.model.workspace;

import javax.persistence.*;
import java.util.UUID;

/**
 * Information about the current review of a workspace.
 *
 * @author Sebastien Gerard
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class AbstractReviewEntity {

    @Id
    private String id;

    @OneToOne
    private WorkspaceEntity workspace;

    AbstractReviewEntity() {
    }

    public AbstractReviewEntity(WorkspaceEntity workspace) {
        this.id = UUID.randomUUID().toString();
        this.workspace = workspace;
    }

    /**
     * Returns the unique id of this review.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique id of this review.
     */
    public AbstractReviewEntity setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Returns the {@link WorkspaceEntity workspace} in review.
     */
    public WorkspaceEntity getWorkspace() {
        return workspace;
    }

    /**
     * Sets the {@link WorkspaceEntity workspace} in review.
     */
    public AbstractReviewEntity setWorkspace(WorkspaceEntity workspace) {
        this.workspace = workspace;
        return this;
    }
}
