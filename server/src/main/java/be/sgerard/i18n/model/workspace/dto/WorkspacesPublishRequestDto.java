package be.sgerard.i18n.model.workspace.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Collection;

import static java.util.Collections.singleton;

/**
 * Request asking the publication of workspaces.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "WorkspacesPublishRequest", description = "Request asking the publication of workspaces.")
@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = WorkspacesPublishRequestDto.Builder.class)
public class WorkspacesPublishRequestDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "List of workspaces ids to publish.", required = true)
    private final Collection<String> workspaces;

    @Schema(description = "Message describing the publication.", required = true)
    private final String message;

    /**
     * Builder of {@link WorkspacesPublishRequestDto workspaces request DTO}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        public Builder workspace(String workspaceId) {
            return workspaces(singleton(workspaceId));
        }
    }
}
