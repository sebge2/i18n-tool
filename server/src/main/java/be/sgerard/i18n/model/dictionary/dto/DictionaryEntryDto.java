package be.sgerard.i18n.model.dictionary.dto;

import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Entry in a dictionary. This entry translate a concept in different locales.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "DictionaryEntry", description = "Entry in a dictionary. This entry translate a concept in different locales.")
@JsonDeserialize(builder = DictionaryEntryDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class DictionaryEntryDto {

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DictionaryEntryEntity entity) {
        return builder(entity, emptySet());
    }

    public static Builder builder(DictionaryEntryEntity entity, Set<String> localeIds) {
        return builder()
                .id(entity.getId())
                .translations(
                        entity.getTranslations().entrySet().stream()
                                .filter(entry -> localeIds.isEmpty() || localeIds.contains(entry.getKey()))
                                .filter(entry -> !isEmpty(entry.getValue()))
                                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue))
                );
    }

    @Schema(description = "The unique id of this entry.", required = true)
    private final String id;

    @Singular
    @Schema(description = "Map associating the locale id and the translation of the related concept.")
    private Map<String, String> translations;

    /**
     * Builder of {@link DictionaryEntryDto dictionary entry}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
