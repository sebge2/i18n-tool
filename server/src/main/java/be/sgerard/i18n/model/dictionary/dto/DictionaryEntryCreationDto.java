package be.sgerard.i18n.model.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;


@Schema(name = "DictionaryEntryCreation", description = "Request asking the creation of a new dictionary entry.")
@JsonDeserialize(builder = DictionaryEntryCreationDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class DictionaryEntryCreationDto {

    public static Builder builder() {
        return new Builder();
    }

    @Schema(description = "Map associating the locale id and the translation of the related concept.", required = true)
    private Map<String, String> translations;

    /**
     * Builder of a {@link DictionaryEntryCreationDto creation request}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
