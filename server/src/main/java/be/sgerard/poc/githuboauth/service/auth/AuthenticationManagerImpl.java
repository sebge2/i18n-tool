package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.model.auth.ExternalUserDto;
import be.sgerard.poc.githuboauth.model.auth.UserDto;
import be.sgerard.poc.githuboauth.model.auth.UserEntity;
import org.springframework.context.event.EventListener;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public UserEntity getCurrentUser() {
        return doGetCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Please authenticate."));
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
    public void onSessionConnectedEvent(SessionDisconnectEvent event) {
        final Optional<UserEntity> user = getUserFromEvent(event);
        System.out.println(user);
    }

    @EventListener
    public void onSessionDisconnectEvent(SessionDisconnectEvent event) {
        final Optional<UserEntity> user = getUserFromEvent(event);

        System.out.println(user);
    }

    private Optional<UserEntity> doGetCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2Authentication) {
            return getUserFromAuthentication((OAuth2Authentication) authentication);
        } else {
            return Optional.empty();
        }
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
