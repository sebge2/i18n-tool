package be.sgerard.i18n.service.translator.snapshot;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.model.translator.snapshot.ExternalTranslatorConfigSnapshotDto;
import be.sgerard.i18n.model.translator.snapshot.ExternalTranslatorGenericRestConfigSnapshotDto;

/**
 * Abstract implementation of {@link ExternalTranslatorConfigSnapshotDtoMapper translator config} snapshot mapper.
 */
public abstract class AbstractExternalTranslatorConfigSnapshotDtoMapper<E extends ExternalTranslatorConfigEntity, D extends ExternalTranslatorConfigSnapshotDto> implements ExternalTranslatorConfigSnapshotDtoMapper<E, D> {

    /**
     * Fills the specified builder with information from the entity.
     */
    protected void fillDtoBuilder(ExternalTranslatorGenericRestConfigSnapshotDto.ExternalTranslatorGenericRestConfigSnapshotDtoBuilder<?, ?> dtoBuilder,
                                  ExternalTranslatorGenericRestConfigEntity entity) {
        dtoBuilder
                .id(entity.getId())
                .label(entity.getLabel())
                .linkUrl(entity.getLinkUrl());
    }

    /**
     * Fills the specified entity with information from the DTO.
     */
    protected void fillEntity(ExternalTranslatorGenericRestConfigEntity entity, ExternalTranslatorGenericRestConfigSnapshotDto dto) {
        entity
                .setId(dto.getId())
                .setLabel(dto.getLabel())
                .setLinkUrl(dto.getLinkUrl());
    }
}
