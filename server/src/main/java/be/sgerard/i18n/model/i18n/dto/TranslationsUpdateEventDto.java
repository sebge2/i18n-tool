package be.sgerard.i18n.model.i18n.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Collection;

/**
 * Event sent when translations have been updated.
 *
 * @author Sebastien Gerard
 */
@Getter
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = TranslationsUpdateEventDto.Builder.class)
public class TranslationsUpdateEventDto {

    @Schema(description = "Id of the editor.", required = true)
    private final String userId;

    @Schema(description = "Display name of the editor.", required = true)
    private final String userDisplayName;

    @Schema(description = "Updated translations.", required = true)
    @Singular
    private final Collection<BundleKeyTranslationDto> translations;

    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }

}
