package be.sgerard.i18n.model.dictionary.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Entry in a dictionary. This entry translate a concept in different locales.
 *
 * @author Sebastien Gerard
 */
@Schema(name = "DictionaryEntryPatch", description = "Patch of an entry in a dictionary. This entry translate a concept in different locales.")
@JsonDeserialize(builder = DictionaryEntryPatchDto.Builder.class)
@Getter
@Builder(builderClassName = "Builder")
public class DictionaryEntryPatchDto {

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Merges the specified {@link DictionaryEntryPatchDto patches}, if two patches apply the same modification (same dictionary entry and same locale),
     * then the last patch wins.
     */
    public static List<DictionaryEntryPatchDto> mergePatches(List<DictionaryEntryPatchDto> patches) {
        final Map<String, Map<String, String>> mergedPatches = new LinkedHashMap<>();

        patches.forEach(patch -> {
            mergedPatches.putIfAbsent(patch.getId(), new LinkedHashMap<>());

            patch.getTranslations().forEach(
                    (locale, translation) -> mergedPatches.get(patch.getId()).put(locale, translation)
            );
        });

        return mergedPatches.entrySet().stream()
                .map(mergedPatch -> new DictionaryEntryPatchDto(mergedPatch.getKey(), mergedPatch.getValue()))
                .collect(toList());
    }

    @Schema(description = "The unique id of this entry.", required = true)
    private final String id;

    @Singular
    @Schema(description = "Map associating the locale id and the translation of the related concept.")
    private Map<String, String> translations;

    /**
     * Builder of {@link DictionaryEntryPatchDto dictionary entry patch}.
     */
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class Builder {
    }
}
