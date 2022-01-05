package be.sgerard.i18n.model.i18n.dto.translation.text;

import be.sgerard.i18n.model.i18n.dto.translate.ExternalTranslationSourceDto;
import be.sgerard.i18n.model.validation.ValidationResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Translations of a text.
 */
@Schema(name = "TextTranslationResponse", description = "Translations of a text. This text may have been translated by different sources.")
@Getter
public class TextTranslationResponseDto {

    /**
     * Returns a new {@link Collector collector} for {@link ValidationResult validation results}.
     */
    public static Collector<TextTranslationResponseDto, List<TextTranslationResponseDto>, TextTranslationResponseDto> toTextTranslationResponse() {
        return new Collector<>() {
            @Override
            public Supplier<List<TextTranslationResponseDto>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<TextTranslationResponseDto>, TextTranslationResponseDto> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<TextTranslationResponseDto>> combiner() {
                return (first, second) -> {
                    first.addAll(second);
                    return first;
                };
            }

            @Override
            public Function<List<TextTranslationResponseDto>, TextTranslationResponseDto> finisher() {
                return (list) -> new TextTranslationResponseDto(
                        list.stream()
                                .map(TextTranslationResponseDto::getExternalSources)
                                .flatMap(Collection::stream)
                                .collect(toSet()),
                        list.stream()
                                .map(TextTranslationResponseDto::getTranslations)
                                .flatMap(Collection::stream)
                                .collect(toList())
                );
            }

            @Override
            public Set<Characteristics> characteristics() {
                return emptySet();
            }
        };
    }

    @Schema(description = "Definition of external translation sources.")
    private final Collection<ExternalTranslationSourceDto> externalSources;

    @Schema(description = "Available translations.")
    private final List<TextTranslationDto> translations;

    @JsonCreator
    public TextTranslationResponseDto(@JsonProperty("externalSources") Collection<ExternalTranslationSourceDto> externalSources,
                                      @JsonProperty("translations") List<TextTranslationDto> translations) {
        this.externalSources = externalSources;

        this.translations = translations;
    }

    public TextTranslationResponseDto(ExternalTranslationSourceDto externalSource, TextTranslationDto translation) {
        this(singleton(externalSource), singletonList(translation));
    }
}
