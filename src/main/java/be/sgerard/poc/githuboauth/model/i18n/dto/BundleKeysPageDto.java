package be.sgerard.poc.githuboauth.model.i18n.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "List of paginated translations.")
public class BundleKeysPageDto {

    public static Builder builder() {
        return new Builder();
    }

    @ApiModelProperty(notes = "Unique identifier of the workspace containing those translations.", required = true)
    private final String workspaceId;

    @ApiModelProperty(notes = "Last key defined in this page. It can be used to call the next page.", required = true, dataType = "java.lang.String")
    private final String lastKey;

    @ApiModelProperty(notes = "All bundle files contained in this page.", required = true)
    private final List<BundleFileDto> files;

    private BundleKeysPageDto(Builder builder) {
        workspaceId = builder.workspaceId;
        lastKey = builder.lastKey;
        files = builder.files;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public Optional<String> getLastKey() {
        return Optional.ofNullable(lastKey);
    }

    public List<BundleFileDto> getFiles() {
        return files;
    }

    public static final class Builder {

        private String workspaceId;
        private String lastKey;
        private List<BundleFileDto> files = new ArrayList<>();

        private Builder() {
        }

        public Builder workspaceId(String workspaceId) {
            this.workspaceId = workspaceId;
            return this;
        }

        public Builder lastKey(String lastKey) {
            this.lastKey = lastKey;
            return this;
        }

        public Builder files(List<BundleFileDto> files) {
            this.files.addAll(files);
            return this;
        }

        public Builder files(BundleFileDto... files) {
            return files(asList(files));
        }

        public BundleKeysPageDto build() {
            return new BundleKeysPageDto(this);
        }
    }
}
