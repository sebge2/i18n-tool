package be.sgerard.i18n.service.user.snapshot;

import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.user.snapshot.ExternalUserSnapshotDto;
import be.sgerard.i18n.model.user.snapshot.UserSnapshotDto;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link UserSnapshotDtoMapper Mapper} for external users.
 *
 * @author Sebastien Gerard
 */
@Component
public class ExternalUserSnapshotDtoMapper extends AbstractUserSnapshotDtoMapper<ExternalUserEntity, ExternalUserSnapshotDto> {

    public ExternalUserSnapshotDtoMapper(TranslationLocaleManager localeManager) {
        super(localeManager);
    }

    @Override
    public boolean support(UserSnapshotDto dto) {
        return dto instanceof ExternalUserSnapshotDto;
    }

    @Override
    public boolean support(UserEntity user) {
        return user instanceof ExternalUserEntity;
    }

    @Override
    public Mono<ExternalUserEntity> mapFromDto(ExternalUserSnapshotDto externalUser) {
        return mapFromDto(
                new ExternalUserEntity(externalUser.getExternalId(), mapFromDto(externalUser.getExternalAuthSystem()))
                        .setAvatarUrl(externalUser.getAvatarUrl()),
                externalUser
        );
    }

    @Override
    public Mono<ExternalUserSnapshotDto> mapToDto(ExternalUserEntity externalUser) {
        return mapToDto(
                ExternalUserSnapshotDto
                        .builder()
                        .avatarUrl(externalUser.getAvatarUrl())
                        .externalAuthSystem(mapToDto(externalUser.getExternalAuthSystem()))
                        .externalId(externalUser.getExternalId()),
                externalUser
        );
    }
}
