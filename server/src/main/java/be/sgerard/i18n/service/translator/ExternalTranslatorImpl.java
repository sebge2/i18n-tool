package be.sgerard.i18n.service.translator;

import be.sgerard.i18n.model.dictionary.ExternalSourceTranslationRequest;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.i18n.dto.translate.ExternalTranslationSourceDto;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationDto;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationRequestDto;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationResponseDto;
import be.sgerard.i18n.service.translator.handler.ExternalTranslatorHandler;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static be.sgerard.i18n.model.i18n.dto.translate.ExternalTranslationSourceDto.fromConfig;
import static be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationResponseDto.toTextTranslationResponse;

/**
 * Implementation of the {@link ExternalTranslator external translator}.
 */
@Service
public class ExternalTranslatorImpl implements ExternalTranslator {

    /**
     * Maximum translations in parallel.
     */
    public static final int MAX_PARALLEL = 3;

    private final ExternalTranslatorHandler<ExternalTranslatorConfigEntity> handler;
    private final ExternalTranslatorConfigManager configManager;
    private final TranslationLocaleManager localeManager;

    public ExternalTranslatorImpl(ExternalTranslatorHandler<ExternalTranslatorConfigEntity> handler,
                                  ExternalTranslatorConfigManager configManager,
                                  TranslationLocaleManager localeManager) {
        this.handler = handler;
        this.configManager = configManager;
        this.localeManager = localeManager;
    }


    @Override
    public Mono<TextTranslationResponseDto> translate(TextTranslationRequestDto requestDto) {
        return this
                .initRequest(requestDto)
                .flatMap(request ->
                        configManager
                                .findAll()
                                .parallel(MAX_PARALLEL)
                                .map(config -> translate(request, config))
                                .sequential()
                                .flatMap(Function.identity())
                                .collectList()
                                .map(responses -> responses.stream().collect(toTextTranslationResponse()))

                );
    }

    /**
     * Initializes the {@link ExternalSourceTranslationRequest translation} request based on its DTO.
     */
    private Mono<ExternalSourceTranslationRequest> initRequest(TextTranslationRequestDto request) {
        return Mono
                .zip(
                        localeManager.findByIdOrDie(request.getFromLocaleId()),
                        localeManager.findByIdOrDie(request.getTargetLocaleId()),
                        (fromLocale, targetLocale) -> new ExternalSourceTranslationRequest(fromLocale, targetLocale, request.getText())
                );
    }

    /**
     * Translates as asked by the specified {@link ExternalSourceTranslationRequest request} using the external translator
     * configured by the specified {@link ExternalTranslatorConfigEntity configuration}.
     */
    private Flux<TextTranslationResponseDto> translate(ExternalSourceTranslationRequest request, ExternalTranslatorConfigEntity config) {
        final ExternalTranslationSourceDto externalSource = fromConfig(config);

        return handler
                .translate(request, config)
                .map(translation -> new TextTranslationResponseDto(
                        externalSource, new TextTranslationDto(config.getId(), null, translation)
                ));
    }
}
