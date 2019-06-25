package be.sgerard.i18n.model.i18n.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "Bundle file containing translations of keys.")
public class BundleFileDto {

    public static Builder builder() {
        return new Builder();
    }

    @ApiModelProperty(notes = "Unique identifier of a bundle file.", required = true)
    private final String id;

    @ApiModelProperty(notes = "Name of this bundle file.", required = true)
    private final String name;

    @ApiModelProperty(notes = "Directory location of this bundle file.", required = true)
    private final String location;

    @ApiModelProperty(notes = "Keys contained in this bundle file.", required = true)
    private final List<BundleKeyDto> keys;

    private BundleFileDto(Builder builder) {
        id = builder.id;
        name = builder.name;
        location = builder.location;
        keys = unmodifiableList(builder.keys);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public List<BundleKeyDto> getKeys() {
        return keys;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String location;
        private final List<BundleKeyDto> keys = new ArrayList<>();

        private Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder location(String val) {
            location = val;
            return this;
        }

        public Builder keys(List<BundleKeyDto> keys) {
            this.keys.addAll(keys);
            return this;
        }

        public Builder keys(BundleKeyDto... keys) {
            return keys(asList(keys));
        }

        public BundleFileDto build() {
            return new BundleFileDto(this);
        }
    }
}
