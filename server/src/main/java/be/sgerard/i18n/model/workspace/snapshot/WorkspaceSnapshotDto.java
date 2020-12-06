package be.sgerard.i18n.model.workspace.snapshot;

import be.sgerard.i18n.model.i18n.snapshot.BundleFileSnapshotDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

/**
 * Dto for storing a {@link WorkspaceEntity workspace} in a snapshot.
 *
 * @author Sebastien Gerard
 */
@JsonDeserialize(builder = WorkspaceSnapshotDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class WorkspaceSnapshotDto {

    /**
     * @see WorkspaceEntity#getId()
     */
    private final String id;

    /**
     * @see WorkspaceEntity#getRepository()
     */
    private final String repository;

    /**
     * @see WorkspaceEntity#getBranch()
     */
    private final String branch;

    /**
     * @see WorkspaceEntity#getStatus()
     */
    private final WorkspaceStatus status;

    /**
     * @see WorkspaceEntity#getFiles()
     */
    private final Collection<BundleFileSnapshotDto> files;

    /**
     * @see WorkspaceEntity#getLastSynchronization()
     */
    private final Instant lastSynchronization;

    /**
     * @see WorkspaceEntity#getReview()
     */
    private final AbstractReviewSnapshotDto review;

    /**
     * @see #lastSynchronization
     */
    public Optional<Instant> getLastSynchronization() {
        return Optional.ofNullable(lastSynchronization);
    }

    /**
     * @see #review
     */
    public Optional<AbstractReviewSnapshotDto> getReview() {
        return Optional.ofNullable(review);
    }

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }

    /**
     * @see be.sgerard.i18n.model.workspace.WorkspaceStatus
     */
    public enum WorkspaceStatus {
        NOT_INITIALIZED,
        INITIALIZED,
        IN_REVIEW
    }

}
