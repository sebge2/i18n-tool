package be.sgerard.i18n.service.translator.listener;

import be.sgerard.i18n.model.translator.dto.ExternalTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.service.translator.dto.ExternalTranslatorConfigDtoMapper;
import be.sgerard.i18n.service.event.EventService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.event.EventType.*;

/**
 * {@link ExternalTranslatorConfigListener Translator config listener} that emits events accordingly.
 */
@Component
public class ExternalTranslatorConfigEventListener implements ExternalTranslatorConfigListener<ExternalTranslatorConfigEntity> {

    private final EventService eventService;
    private final ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto> dtoMapper;

    public ExternalTranslatorConfigEventListener(EventService eventService, ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto> dtoMapper) {
        this.eventService = eventService;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public boolean support(ExternalTranslatorConfigEntity config) {
        return true;
    }

    @Override
    public Mono<Void> afterPersist(ExternalTranslatorConfigEntity config) {
        return Mono
                .just(dtoMapper.mapToDto(config))
                .flatMap(dto -> eventService.broadcastEvent(ADDED_EXTERNAL_TRANSLATOR_CONFIG, dto));
    }

    @Override
    public Mono<Void> afterUpdate(ExternalTranslatorConfigEntity config) {
        return Mono
                .just(dtoMapper.mapToDto(config))
                .flatMap(dto -> eventService.broadcastEvent(UPDATED_EXTERNAL_TRANSLATOR_CONFIG, dto));
    }

    @Override
    public Mono<Void> afterDelete(ExternalTranslatorConfigEntity config) {
        return Mono
                .just(dtoMapper.mapToDto(config))
                .flatMap(dto -> eventService.broadcastEvent(DELETED_EXTERNAL_TRANSLATOR_CONFIG, dto));
    }
}
