package be.sgerard.i18n.service.translator.dto;

import be.sgerard.i18n.model.translator.dto.ExternalTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;

/**
 * Abstract {@link ExternalTranslatorConfigDtoMapper translator config DTO mapper}.
 *
 * @author Sebastien Gerard
 */
public abstract class AbstractExternalTranslatorConfigDtoMapper<C extends ExternalTranslatorConfigEntity, D extends ExternalTranslatorConfigDto> implements ExternalTranslatorConfigDtoMapper<C, D> {

    /**
     * Fills the builder with the specified entity.
     */
    protected <B extends ExternalTranslatorConfigDto.BaseBuilder<?, ?>> B fillDtoBuilder(B builder, C config) {
        builder
                .id(config.getId())
                .label(config.getLabel())
                .linkUrl(config.getLinkUrl());

        return builder;
    }

    /**
     * Fills the specified entity with the state of the specified DTO.
     */
    protected C fillEntity(C config, D dto){
        config
                .setLabel(dto.getLabel())
                .setLinkUrl(dto.getLinkUrl());

        return config;
    }
}
