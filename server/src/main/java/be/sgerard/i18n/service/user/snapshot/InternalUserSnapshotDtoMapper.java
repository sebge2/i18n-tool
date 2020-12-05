package be.sgerard.i18n.service.user.snapshot;

import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.user.snapshot.InternalUserSnapshotDto;
import be.sgerard.i18n.model.user.snapshot.UserSnapshotDto;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;

/**
 * {@link UserSnapshotDtoMapper Mapper} for internal users.
 *
 * @author Sebastien Gerard
 */
@Component
public class InternalUserSnapshotDtoMapper extends AbstractUserSnapshotDtoMapper<InternalUserEntity, InternalUserSnapshotDto>  {

    public InternalUserSnapshotDtoMapper(TranslationLocaleManager localeManager) {
        super(localeManager);
    }

    @Override
    public boolean support(UserSnapshotDto dto) {
        return dto instanceof InternalUserSnapshotDto;
    }

    @Override
    public boolean support(UserEntity user) {
        return user instanceof InternalUserEntity;
    }

    @Override
    public Mono<InternalUserEntity> mapFromDto(InternalUserSnapshotDto internalUser) {
        return mapFromDto(
                new InternalUserEntity(internalUser.getUsername(), internalUser.getDisplayName())
                        .setAvatar(internalUser.getAvatar().map(avatar -> Base64.getDecoder().decode(avatar)).orElse(new byte[0]))
                        .setPassword(internalUser.getPassword()),
                internalUser
        );
    }

    @Override
    public Mono<InternalUserSnapshotDto> mapToDto(InternalUserEntity internalUser) {
        return mapToDto(
                InternalUserSnapshotDto
                        .builder()
                        .avatar(internalUser.getAvatar().filter(bytes -> bytes.length > 0).map(bytes -> Base64.getEncoder().encodeToString(bytes)).orElse(null))
                        .password(internalUser.getPassword()),
                internalUser
        );
    }
}
