package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.model.security.auth.ExternalOAuth2AuthenticatedUser;
import be.sgerard.i18n.model.security.user.ExternalUserDto;
import be.sgerard.i18n.model.security.user.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.UserEntity;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.user.UserManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final UserManager userManager;

    public AuthenticationManagerImpl(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public ExternalOAuth2AuthenticatedUser initAuthenticatedUser(ExternalUserEntity currentUser, ExternalUserDto externalUserDto) {
        final Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.addAll(currentUser.getRoles().stream().map(UserRole::toAuthority).collect(toSet()));
        authorities.addAll(externalUserDto.getRoles().stream().map(UserRole::toAuthority).collect(toSet()));

        return new ExternalOAuth2AuthenticatedUser(currentUser.getId(), externalUserDto.getGitHubToken().orElse(null), authorities);
    }

    @Override
    public Optional<AuthenticatedUser> getCurrentAuthenticatedUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof AuthenticatedUser) {
            return Optional.of((AuthenticatedUser) authentication.getPrincipal());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public UserEntity getCurrentUserOrFail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof AuthenticatedUser) {
            return getUserFromAuthentication((AuthenticatedUser) authentication.getPrincipal())
                    .orElseThrow(() -> new AccessDeniedException("Please authenticate."));
        } else {
            throw new AccessDeniedException("Please authenticate.");
        }
    }

    @Override
    public UserEntity getUserFromPrincipal(Principal principal) {
        if (!(principal instanceof AuthenticatedUser)) {
            throw new IllegalArgumentException("The principal is not of the expected format. " +
                    "Hint: are you sure the authentication is valid?");
        }

        return getUserFromAuthentication((AuthenticatedUser) principal)
                .orElseThrow(() -> new IllegalStateException("There is no user for principal [" + principal + "]."));
    }

    private Optional<UserEntity> getUserFromAuthentication(AuthenticatedUser user) {
        return Optional.ofNullable(user)
                .flatMap(dto -> userManager.getUserById(dto.getUserId()));
    }
}
