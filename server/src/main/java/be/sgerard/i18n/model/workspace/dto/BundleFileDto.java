package be.sgerard.i18n.model.workspace.dto;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Bundle file containing translations of keys.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "BundleFile", description = "Bundle file containing translations of keys.")
@JsonDeserialize(builder = BundleFileDto.Builder.class)
@Getter
public class BundleFileDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BundleFileEntity entity) {
        return builder()
                .id(entity.getId())
                .name(entity.getName())
                .location(entity.getLocation());
    }

    @Schema(description = "Unique identifier of a bundle file.", required = true)
    private final String id;

    @Schema(description = "Name of this bundle file.", required = true)
    private final String name;

    @Schema(description = "Directory location of this bundle file.", required = true)
    private final String location;

    private BundleFileDto(Builder builder) {
        id = builder.id;
        name = builder.name;
        location = builder.location;
    }

    /**
     * Builder of {@link BundleFileDto bundle file.}
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {

        private String id;
        private String name;
        private String location;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public BundleFileDto build() {
            return new BundleFileDto(this);
        }
    }
}
