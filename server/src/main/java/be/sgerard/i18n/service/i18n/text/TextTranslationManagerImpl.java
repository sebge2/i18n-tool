package be.sgerard.i18n.service.i18n.text;

import be.sgerard.i18n.model.dictionary.DictionaryEntrySearchRequest;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationDto;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationRequestDto;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationResponseDto;
import be.sgerard.i18n.service.dictionary.DictionaryManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Implementation of the {@link TextTranslationManager text translation manager}.
 */
@Service
public class TextTranslationManagerImpl implements TextTranslationManager {

    private final DictionaryManager dictionaryManager;

    public TextTranslationManagerImpl(DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
    }

    @Override
    public Mono<TextTranslationResponseDto> translate(TextTranslationRequestDto request) {
        return this
                .translateFromDictionary(request)
                .map(internalTranslations -> new TextTranslationResponseDto(
                        emptyList(),
                        internalTranslations
                ));
    }

    /**
     * Translates using the internal {@link DictionaryManager dictionary manager}.
     */
    private Mono<List<TextTranslationDto>> translateFromDictionary(TextTranslationRequestDto request) {
        return dictionaryManager
                .find(
                        DictionaryEntrySearchRequest.builder()
                                .translation(new DictionaryEntrySearchRequest.TranslationRestriction(
                                        request.getText(),
                                        request.getFromLocaleId()
                                ))
                                .build()
                )
                .filter(entry -> entry.getTranslation(request.getTargetLocaleId()).isPresent())
                .map(entry -> new TextTranslationDto(
                        null,
                        entry.getTranslation(request.getFromLocaleId()).orElse(null),
                        entry.getTranslation(request.getTargetLocaleId()).orElse(null)
                ))
                .collectList();
    }
}
