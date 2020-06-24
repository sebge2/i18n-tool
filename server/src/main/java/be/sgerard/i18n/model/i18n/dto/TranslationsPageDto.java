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
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Page of translations.
 *
 * @author Sebastien Gerard
 */
@ApiModel(description = "List of paginated translations.")
@JsonDeserialize(builder = TranslationsPageDto.Builder.class)
public class TranslationsPageDto {

    public static Builder builder() {
        return new Builder();
    }

    @ApiModelProperty(notes = "Last key defined in this page. It can be used to call the next page.", required = true, dataType = "java.lang.String")
    private final String lastKey;

    @ApiModelProperty(notes = "Workspaces", required = true)
    private final List<TranslationsWorkspaceDto> workspaces;

    private TranslationsPageDto(Builder builder) {
        lastKey = builder.lastKey;
        workspaces = unmodifiableList(builder.workspaces);
    }

    /**
     * Returns {@link TranslationsWorkspaceDto translations of a workspace.}
     */
    public List<TranslationsWorkspaceDto> getWorkspaces() {
        return workspaces;
    }

    /**
     * Returns the last translation key of this page.
     *
     * @see TranslationsSearchRequestDto#getLastKey()
     */
    public Optional<String> getLastKey() {
        return Optional.ofNullable(lastKey);
    }

    /**
     * Builder of {@link TranslationsPageDto bundle keys page}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private final List<TranslationsWorkspaceDto> workspaces = new ArrayList<>();
        private String lastKey;

        private Builder() {
        }

        public Builder lastKey(String lastKey) {
            this.lastKey = lastKey;
            return this;
        }

        @JsonProperty("workspaces")
        public Builder workspaces(List<TranslationsWorkspaceDto> workspaces) {
            this.workspaces.addAll(workspaces);
            return this;
        }

        @JsonIgnore
        public Builder workspaces(TranslationsWorkspaceDto... workspaces) {
            return workspaces(asList(workspaces));
        }

        public TranslationsPageDto build() {
            return new TranslationsPageDto(this);
        }
    }
}
