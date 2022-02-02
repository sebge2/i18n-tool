package be.sgerard.i18n.service.translator.dto;

import be.sgerard.i18n.model.translator.dto.ExternalTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Composite {@link ExternalTranslatorConfigDtoMapper dto mapper}.
 */
@Primary
@Component
public class CompositeExternalTranslatorConfigDtoMapper implements ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto> {

    private final List<ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto>> mappers;

    @SuppressWarnings("unchecked")
    public CompositeExternalTranslatorConfigDtoMapper(List<ExternalTranslatorConfigDtoMapper<?, ?>> mappers) {
        this.mappers = (List<ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto>>) (List<?>) mappers;
    }

    @Override
    public boolean support(ExternalTranslatorConfigType configType) {
        return findMapper(configType).isPresent();
    }

    @Override
    public ExternalTranslatorConfigDto mapToDto(ExternalTranslatorConfigEntity entity) {
        return findMapperOrDie(entity.getType()).mapToDto(entity);
    }

    @Override
    public ExternalTranslatorConfigEntity mapFromDto(ExternalTranslatorConfigDto dto) {
        return findMapperOrDie(dto.getType()).mapFromDto(dto);
    }

    @Override
    public ExternalTranslatorConfigEntity fillFromDto(ExternalTranslatorConfigDto dto, ExternalTranslatorConfigEntity entity) {
        return findMapperOrDie(dto.getType()).fillFromDto(dto, entity);
    }

    /**
     * Finds the mapper supporting the specified {@link ExternalTranslatorConfigType config type}.
     */
    private Optional<ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto>> findMapper(ExternalTranslatorConfigType type) {
        return mappers.stream()
                .filter(mapper -> mapper.support(type))
                .findFirst();
    }

    /**
     * Finds the mapper supporting the specified {@link ExternalTranslatorConfigType config type}.
     */
    private ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto> findMapperOrDie(ExternalTranslatorConfigType type) {
        return findMapper(type)
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported external configuration [" + type + "]. Hint: make sure that all mappers have been registered."));
    }
}
