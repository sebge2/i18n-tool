package be.sgerard.i18n.service.security.session;

import be.sgerard.i18n.model.security.session.dto.UserLiveSessionDto;
import be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Mapper of {@link UserLiveSessionEntity user live sessions} to {@link UserLiveSessionDto DTOs}.
 *
 * @author Sebastien Gerard
 */
@Component
public class UserLiveSessionDtoMapper {

    private final UserManager userManager;

    public UserLiveSessionDtoMapper(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Maps to the DTO.
     */
    public Mono<UserLiveSessionDto> toDto(UserLiveSessionEntity userLiveSession) {
        return userManager
                .findByIdOrDie(userLiveSession.getUserId())
                .map(user ->
                        UserLiveSessionDto.builder()
                                .id(userLiveSession.getId())
                                .user(UserDto.builder(user).build())
                                .build()
                );
    }
}
