package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.security.session.dto.UserLiveSessionDto;
import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.service.user.UserManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static be.sgerard.i18n.model.security.session.dto.UserLiveSessionDto.builder;

/**
 * Mapper from {@link UserLiveSessionEntity session entity} to {@link UserLiveSessionDto session DTO.
 *
 * @author Sebastien Gerard
 */
@Component
@AllArgsConstructor
public class UserLiveSessionDtoMapper {

    private final UserManager userManager;

    /**
     * Maps the specified {@link UserLiveSessionEntity entity} to its {@link UserLiveSessionDto DTO representation}.
     */
    public Mono<UserLiveSessionDto> toDto(UserLiveSessionEntity userLiveSession) {
        return userManager
                .findByIdOrDie(userLiveSession.getUser())
                .map(user ->
                        builder()
                                .id(userLiveSession.getId())
                                .userId(userLiveSession.getUser())
                                .userDisplayName(user.getDisplayName())
                                .build()
                );
    }
}
