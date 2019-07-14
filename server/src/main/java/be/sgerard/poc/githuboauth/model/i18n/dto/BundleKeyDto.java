package be.sgerard.poc.githuboauth.model.i18n.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * @author Sebastien Gerard
 */
@ApiModel(description = "Key in a bundle file associated to translations.")
public class BundleKeyDto {

    @ApiModelProperty(notes = "Unique identifier of a key.", required = true)
    private final String id;

    @ApiModelProperty(notes = "Key associated to translation entries.", required = true)
    private final String key;

    @ApiModelProperty(notes = "All translations associated to this key.", required = true)
    private final List<BundleKeyEntryDto> entries;

    private BundleKeyDto(Builder builder) {
        id = builder.id;
        key = builder.key;
        entries = unmodifiableList(builder.entries);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public List<BundleKeyEntryDto> getEntries() {
        return entries;
    }

    public static final class Builder {
        private String id;
        private String key;
        private final List<BundleKeyEntryDto> entries = new ArrayList<>();

        private Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder key(String val) {
            key = val;
            return this;
        }

        public Builder entries(List<BundleKeyEntryDto> val) {
            entries.addAll(val);
            return this;
        }

        public Builder entries(BundleKeyEntryDto... val) {
            return entries(asList(val));
        }

        public BundleKeyDto build() {
            return new BundleKeyDto(this);
        }
    }
}
