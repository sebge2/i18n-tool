package be.sgerard.i18n.model.workspace.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Information about the current review of a workspace.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "WorkspaceReview", description = "Information about the current review of a workspace.")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GitHubReviewDto.class, name = "GIT_HUB")
})
public interface ReviewDto {

    /**
     * Returns the {@link ReviewType type} of review.
     */
    ReviewType getType();

    /**
     * All review instance types.
     */
    enum ReviewType {

        GIT_HUB
    }
}
