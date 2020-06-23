package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * List of translations of a workspace.
 *
 * @author Sebastien Gerard
 */
@ApiModel(description = "List of translations of a workspace.")
@JsonDeserialize(builder = TranslationsWorkspaceDto.Builder.class)
public class TranslationsWorkspaceDto {

    public static Builder builder() {
        return new Builder();
    }

    @ApiModelProperty(notes = "Unique identifier of the workspace containing those translations.", required = true)
    private final String workspaceId;

    @ApiModelProperty(notes = "All bundle files contained in this workspace page.", required = true)
    private final List<BundleFileDto> files;

    private TranslationsWorkspaceDto(Builder builder) {
        workspaceId = builder.workspaceId;
        files = unmodifiableList(builder.files);
    }

    /**
     * Returns the id of the associated workspace.
     */
    public String getWorkspaceId() {
        return workspaceId;
    }

    /**
     * Returns {@link BundleFileDto files} of this workspace.
     */
    public List<BundleFileDto> getFiles() {
        return files;
    }

    /**
     * Builder of {@link TranslationsWorkspaceDto translation workspace}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String workspaceId;
        private final List<BundleFileDto> files = new ArrayList<>();

        private Builder() {
        }

        public Builder workspaceId(String workspaceId) {
            this.workspaceId = workspaceId;
            return this;
        }

        @JsonProperty("files")
        public Builder files(List<BundleFileDto> files) {
            this.files.addAll(files);
            return this;
        }

        @JsonIgnore
        public Builder files(BundleFileDto... files) {
            return files(asList(files));
        }

        public TranslationsWorkspaceDto build() {
            return new TranslationsWorkspaceDto(this);
        }
    }
}
