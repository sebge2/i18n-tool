package be.sgerard.i18n.model.workspace.snapshot;

import be.sgerard.i18n.model.repository.snapshot.RepositorySnapshotDto;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Snapshot of a {@link be.sgerard.i18n.model.workspace.persistence.AbstractReviewEntity review}.
 *
 * @author Sebastien Gerard
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitHubReviewSnapshotDto.class, name = "GIT_HUB")
})
public abstract class AbstractReviewSnapshotDto {

    /**
     * Returns the type of review.
     */
    public abstract RepositorySnapshotDto.Type getType();
}
