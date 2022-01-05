package be.sgerard.i18n.service.translator;

import be.sgerard.i18n.model.translator.dto.ExternalTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.repository.dictionary.ExternalTranslatorConfigRepository;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.translator.dto.ExternalTranslatorConfigDtoMapper;
import be.sgerard.i18n.service.translator.listener.ExternalTranslatorConfigListener;
import be.sgerard.i18n.service.translator.validation.ExternalTranslatorConfigValidator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementation of the {@link ExternalTranslatorConfigManager configuration manager}.
 */
@Service
public class ExternalTranslatorConfigManagerImpl implements ExternalTranslatorConfigManager {

    private final ExternalTranslatorConfigRepository repository;
    private final ExternalTranslatorConfigValidator<ExternalTranslatorConfigEntity> validator;
    private final ExternalTranslatorConfigListener<ExternalTranslatorConfigEntity> listener;
    private final ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto> dtoMapper;

    public ExternalTranslatorConfigManagerImpl(ExternalTranslatorConfigRepository repository,
                                               ExternalTranslatorConfigValidator<ExternalTranslatorConfigEntity> validator,
                                               ExternalTranslatorConfigListener<ExternalTranslatorConfigEntity> listener,
                                               ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto> dtoMapper) {
        this.repository = repository;
        this.validator = validator;
        this.listener = listener;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Flux<ExternalTranslatorConfigEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<ExternalTranslatorConfigEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<ExternalTranslatorConfigEntity> create(ExternalTranslatorConfigDto config) {
        return create(dtoMapper.mapFromDto(config));
    }

    @Override
    public Mono<ExternalTranslatorConfigEntity> create(ExternalTranslatorConfigEntity config) {
        return Mono.just(config)
                .flatMap(conf ->
                        validator
                                .beforePersistOrUpdate(conf)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return conf;
                                })
                )
                .flatMap(repository::save)
                .then(listener.afterUpdate(config).thenReturn(config));
    }

    @Override
    public Mono<ExternalTranslatorConfigEntity> update(ExternalTranslatorConfigDto dto) {
        return findByIdOrDie(dto.getId())
                .map(config -> dtoMapper.fillFromDto(dto, config))
                .flatMap(config ->
                        validator
                                .beforePersistOrUpdate(config)
                                .map(validationResult -> {
                                    ValidationException.throwIfFailed(validationResult);

                                    return config;
                                })
                )
                .flatMap(repository::save)
                .flatMap(conf -> listener.afterUpdate(conf).thenReturn(conf));
    }

    @Override
    public Mono<ExternalTranslatorConfigEntity> delete(String id) {
        return findById(id)
                .flatMap(config ->
                        repository.delete(config).thenReturn(config)
                )
                .flatMap(config -> listener.afterDelete(config).thenReturn(config));
    }
}
