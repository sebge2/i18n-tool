package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.ExternalKeyAuthenticatedUser;
import be.sgerard.i18n.model.security.auth.ExternalOAuth2AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.security.user.dto.ExternalUserDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.event.InternalEventListener;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static be.sgerard.i18n.model.event.EventType.*;
import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.getAuthenticatedUser;
import static be.sgerard.i18n.service.security.auth.AuthenticationUtils.updateAuthentication;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository.DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME;

/**
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final PasswordEncoder passwordEncoder;
    private final FindByIndexNameSessionRepository<Session> sessionRepository;
    private final EventService eventService;

    @SuppressWarnings("unchecked")
    public AuthenticationManagerImpl(PasswordEncoder passwordEncoder,
                                     FindByIndexNameSessionRepository<?> sessionRepository,
                                     EventService eventService) {
        this.passwordEncoder = passwordEncoder;
        this.sessionRepository = (FindByIndexNameSessionRepository<Session>) sessionRepository;
        this.eventService = eventService;

        // TODO
//        this.eventService.addListener(new UserUpdateEventListener());
//        this.eventService.addListener(new UserDeletedEventListener());
    }

    @Override
    @Transactional(readOnly = true)
    public ExternalOAuth2AuthenticatedUser initExternalOAuthUser(ExternalUserEntity currentUser, ExternalUserDto externalUserDto) {
        final Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.addAll(currentUser.getRoles().stream().map(UserRole::toAuthority).collect(toSet()));
        authorities.addAll(externalUserDto.getRoles().stream().map(UserRole::toAuthority).collect(toSet()));

        return new ExternalOAuth2AuthenticatedUser(
                UUID.randomUUID().toString(),
                UserDto.builder(currentUser).build(),
                externalUserDto.getGitHubToken().orElse(null),
                authorities
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ExternalKeyAuthenticatedUser initExternalKeyUser(ExternalUserEntity currentUser, ExternalUserDto externalUserDto) {
        final Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.addAll(currentUser.getRoles().stream().map(UserRole::toAuthority).collect(toSet()));
        authorities.addAll(externalUserDto.getRoles().stream().map(UserRole::toAuthority).collect(toSet()));

        return new ExternalKeyAuthenticatedUser(
                UUID.randomUUID().toString(),
                UserDto.builder(currentUser).build(),
                passwordEncoder.encode(""),
                externalUserDto.getGitHubToken().orElse(null),
                authorities
        );
    }

    @Override
    @Transactional(readOnly = true)
    public InternalAuthenticatedUser initInternalUser(InternalUserEntity currentUser) {
        return new InternalAuthenticatedUser(
                UUID.randomUUID().toString(),
                UserDto.builder(currentUser).build(),
                currentUser.getPassword(),
                null,
                currentUser.getRoles().stream().map(UserRole::toAuthority).collect(toSet())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuthenticatedUser> getCurrentUser() {
        return getAuthenticatedUser(SecurityContextHolder.getContext().getAuthentication());
    }

    private final class UserUpdateEventListener implements InternalEventListener<UserDto> {

        private UserUpdateEventListener() {
        }

        @Override
        public boolean support(EventType eventType) {
            return eventType == UPDATED_USER;
        }

        @Override
        public void onEvent(UserDto updatedUser) {
            sessionRepository.findByPrincipalName(updatedUser.getId()).values().stream()
                    .map(Session.class::cast)
                    .filter(session -> session.getAttributeNames().contains(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME))
                    .filter(session -> ((SecurityContext) session.getAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME)).getAuthentication().getPrincipal() instanceof AuthenticatedUser)
                    .forEach(session -> update(session, updatedUser));
        }

        private void update(Session session, UserDto updatedUser) {
            final SecurityContext securityContext = session.getAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME);
            final AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getAuthentication().getPrincipal();

            final AuthenticatedUser updatedAuthenticatedUser = updateAuthenticatedUser(authenticatedUser, updatedUser);
            final AuthenticatedUserDto updatedAuthenticatedUserDto = AuthenticatedUserDto.builder(updatedAuthenticatedUser).build();

            final SecurityContextImpl updatedSecurityContext = new SecurityContextImpl();
            updatedSecurityContext.setAuthentication(updateAuthentication(securityContext.getAuthentication(), updatedAuthenticatedUser));
            session.setAttribute(DEFAULT_SPRING_SECURITY_CONTEXT_ATTR_NAME, updatedSecurityContext);

            sessionRepository.save(session);

//            eventService.broadcastInternally(UPDATED_AUTHENTICATED_USER, updatedAuthenticatedUserDto);
            eventService.sendEventToUsers(UserRole.ADMIN, UPDATED_AUTHENTICATED_USER, updatedAuthenticatedUserDto);
        }

        private AuthenticatedUser updateAuthenticatedUser(AuthenticatedUser authenticatedUser, UserDto updatedUser) {
            return authenticatedUser.updateSessionRoles(
                    Stream
                            .concat(
                                    authenticatedUser.getSessionRoles().stream().filter(role -> !role.isAssignableByEndUser()),
                                    updatedUser.getRoles().stream()
                            )
                            .collect(toList())
            );
        }
    }

    private final class UserDeletedEventListener implements InternalEventListener<UserDto> {

        private UserDeletedEventListener() {
        }

        @Override
        public boolean support(EventType eventType) {
            return eventType == DELETED_USER;
        }

        @Override
        public void onEvent(UserDto userDto) {
            sessionRepository.findByPrincipalName(userDto.getId()).values().stream()
                    .map(Session.class::cast)
                    .forEach(session -> sessionRepository.deleteById(session.getId()));
        }
    }

}
