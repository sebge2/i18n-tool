package be.sgerard.poc.githuboauth.service.security.auth;

import be.sgerard.poc.githuboauth.model.security.user.ExternalUserDto;
import be.sgerard.poc.githuboauth.model.security.user.UserDto;
import be.sgerard.poc.githuboauth.model.security.user.UserEntity;
import be.sgerard.poc.githuboauth.service.security.user.UserRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.NativeMessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final OAuth2ClientContext context;
    private final UserRepository userRepository;

    public AuthenticationManagerImpl(OAuth2ClientContext context,
                                     UserRepository userRepository) {
        this.context = context;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2Authentication) {
            return getUserFromAuthentication((OAuth2Authentication) authentication);
        } else {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserById(String id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserEntity createOrUpdateUser(ExternalUserDto externalUser) {
        final UserEntity userEntity = userRepository.findByExternalId(externalUser.getExternalId())
                .orElseGet(() -> new UserEntity(externalUser.getExternalId()));

        userEntity.setUsername(externalUser.getUsername());
        userEntity.setAvatarUrl(externalUser.getAvatarUrl());
        userEntity.setEmail(externalUser.getEmail());

        userRepository.save(userEntity);

        return userEntity;
    }

    @Override
    public String getAuthToken() throws AccessDeniedException {
        return context.getAccessToken().getValue();
    }

    @Override
    public boolean isAuthenticated() {
        return (context.getAccessToken() != null) && context.getAccessToken().isExpired();
    }

    @Override
    public Collection<String> getCurrentUserRoles() throws AccessDeniedException {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2Authentication) {
            return ((OAuth2Authentication) authentication).getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList());
        } else {
            throw new AccessDeniedException("Please authenticate.");
        }
    }

    @EventListener
    public void onSessionConnectedEvent(SessionConnectedEvent event) {
        final MessageHeaderAccessor accessor = NativeMessageHeaderAccessor.getAccessor(event.getMessage(), SimpMessageHeaderAccessor.class);
        final String sessionId = SimpMessageHeaderAccessor.getSessionId(accessor.getMessageHeaders());

        final Optional<UserEntity> user = getUserFromEvent(event);

        System.out.println(sessionId);
    }

    @EventListener
    public void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        final Optional<UserEntity> user = getUserFromEvent(event);

        System.out.println(user);
    }

    private Optional<UserEntity> getUserFromEvent(AbstractSubProtocolEvent event) {
        if (!(event.getUser() instanceof OAuth2Authentication)) {
            return Optional.empty();
        }

        return getUserFromAuthentication((OAuth2Authentication) event.getUser());
    }

    private Optional<UserEntity> getUserFromAuthentication(OAuth2Authentication user) {
        return Optional.ofNullable(user)
                .map(dto -> (UserDto) dto.getPrincipal())
                .flatMap(dto -> getUserById(dto.getId()));
    }
}
