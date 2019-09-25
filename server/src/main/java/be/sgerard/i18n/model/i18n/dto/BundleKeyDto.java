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
@ApiModel(description = "Key in a bundle file associated to translations.")
public class BundleKeyDto {

    @ApiModelProperty(notes = "Unique identifier of a key.", required = true)
    private final String id;

    @ApiModelProperty(notes = "Key associated to translation entries.", required = true)
    private final String key;

    @ApiModelProperty(notes = "All translations associated to this key.", required = true)
    private final List<BundleKeyTranslationDto> translations;

    private BundleKeyDto(Builder builder) {
        id = builder.id;
        key = builder.key;
        translations = unmodifiableList(builder.translations);
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

    public List<BundleKeyTranslationDto> getTranslations() {
        return translations;
    }

    public static final class Builder {
        private String id;
        private String key;
        private final List<BundleKeyTranslationDto> translations = new ArrayList<>();

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder translations(List<BundleKeyTranslationDto> translations) {
            this.translations.addAll(translations);
            return this;
        }

        public Builder translations(BundleKeyTranslationDto... translations) {
            return translations(asList(translations));
        }

        public BundleKeyDto build() {
            return new BundleKeyDto(this);
        }
    }
}
