package be.sgerard.i18n.model.workspace.dto;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntryEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * File composing a {@link BundleFileEntity bundle file}.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "BundleFileEntry", description = "File composing a bundle file")
@JsonDeserialize(builder = BundleFileEntryDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleFileEntryDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BundleFileEntryEntity entity) {
        return builder()
                .id(entity.getId())
                .locale(entity.getLocale())
                .file(entity.getFile());
    }

    @Schema(description = "Unique identifier of a bundle file entry.", required = true)
    private final String id;

    @Schema(description = "The unique id of the locale associated to this file", required = true)
    private final String locale;

    @Schema(description = "The file part of the bundle file.", required = true)
    private final String file;

    /**
     * Builder of {@link BundleFileEntryDto bundle file entry.}
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
