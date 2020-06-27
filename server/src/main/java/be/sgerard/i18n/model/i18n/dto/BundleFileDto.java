package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * Bundle file containing translations of keys.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "BundleFile", description = "Bundle file containing translations of keys.")
@JsonDeserialize(builder = BundleFileDto.Builder.class)
public class BundleFileDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Unique identifier of a bundle file.", required = true)
    private final String id;

    @Schema(description = "Name of this bundle file.", required = true)
    private final String name;

    @Schema(description = "Directory location of this bundle file.", required = true)
    private final String location;

    @Schema(description = "Keys contained in this bundle file.", required = true)
    private final List<BundleKeyDto> keys;

    private BundleFileDto(Builder builder) {
        id = builder.id;
        name = builder.name;
        location = builder.location;
        keys = unmodifiableList(builder.keys);
    }

    /**
     * Returns the unique identifier of a bundle file.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of this bundle file.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the directory location of this bundle file.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns {@link BundleKeyDto keys} contained in this bundle file.
     */
    public List<BundleKeyDto> getKeys() {
        return keys;
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
        private final List<BundleKeyDto> keys = new ArrayList<>();

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

        @JsonProperty("keys")
        public Builder keys(List<BundleKeyDto> keys) {
            this.keys.addAll(keys);
            return this;
        }

        @JsonIgnore
        public Builder keys(BundleKeyDto... keys) {
            return keys(asList(keys));
        }

        public BundleFileDto build() {
            return new BundleFileDto(this);
        }
    }
}
