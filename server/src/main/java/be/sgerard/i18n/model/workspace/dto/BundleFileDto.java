package be.sgerard.i18n.model.workspace.dto;

import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Bundle file containing translations of keys.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "BundleFile", description = "Bundle file containing translations of keys.")
@JsonDeserialize(builder = BundleFileDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class BundleFileDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BundleFileEntity bundleFile) {
        return builder()
                .id(bundleFile.getId())
                .name(bundleFile.getName())
                .location(bundleFile.getLocation())
                .type(bundleFile.getType())
                .files(bundleFile.getFiles().stream().map(entry -> BundleFileEntryDto.builder(entry).build()).collect(toList()));
    }

    @Schema(description = "Unique identifier of a bundle file.", required = true)
    private final String id;

    @Schema(description = "Name of this bundle file.", required = true)
    private final String name;

    @Schema(description = "Directory location of this bundle file.", required = true)
    private final String location;

    @Schema(description = "Type of bundle file", required = true)
    private final BundleType type;

    @Schema(description = "All the file paths of this bundle.", required = true)
    @Singular
    private final List<BundleFileEntryDto> files;

    /**
     * Builder of {@link BundleFileDto bundle file.}
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
