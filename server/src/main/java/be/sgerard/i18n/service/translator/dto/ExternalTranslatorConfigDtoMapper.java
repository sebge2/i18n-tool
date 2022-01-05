package be.sgerard.i18n.service.translator.dto;

import be.sgerard.i18n.model.translator.dto.ExternalTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigType;

/**
 * Mapper for the {@link ExternalTranslatorConfigDto repository DTO}.
 *
 * @author Sebastien Gerard
 */
public interface ExternalTranslatorConfigDtoMapper<C extends ExternalTranslatorConfigEntity, D extends ExternalTranslatorConfigDto> {

    /**
     * Returns whether this mapper support the specified config.
     */
    boolean support(ExternalTranslatorConfigType configType);

    /**
     * Maps the specified entity to a DTO.
     */
    D mapToDto(C entity);

    /**
     * Maps the entity from its DTO representation.
     */
    C mapFromDto(D dto);

    /**
     * Fills the entity from its DTO representation.
     */
    C fillFromDto(D dto, C entity);
}
