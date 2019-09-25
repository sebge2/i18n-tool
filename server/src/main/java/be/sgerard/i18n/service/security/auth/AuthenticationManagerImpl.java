package be.sgerard.i18n.service.security.auth;

import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.model.security.user.UserEntity;
import be.sgerard.i18n.service.security.user.UserManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collection;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final OAuth2ClientContext context;
    private final UserManager userManager;

    public AuthenticationManagerImpl(OAuth2ClientContext context,
                                     UserManager userManager) {
        this.context = context;
        this.userManager = userManager;
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
    public UserEntity getCurrentUserOrFail() {
        return getCurrentUser()
                .orElseThrow(() -> new AccessDeniedException("Please authenticate."));
    }

    @Override
    public UserEntity getUserFromPrincipal(Principal principal) {
        if (!(principal instanceof OAuth2Authentication)) {
            throw new IllegalArgumentException("The principal is not of the expected format. " +
                    "Hint: are you sure the authentication is valid?");
        }

        return getUserFromAuthentication((OAuth2Authentication) principal)
                .orElseThrow(() -> new IllegalStateException("There is no user for principal [" + principal + "]."));
    }

    @Override
    public String getAuthToken() throws AccessDeniedException {
        return context.getAccessToken().getValue();
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

    private Optional<UserEntity> getUserFromAuthentication(OAuth2Authentication user) {
        return Optional.ofNullable(user)
                .map(dto -> (UserDto) dto.getPrincipal())
                .flatMap(dto -> userManager.getUserById(dto.getId()));
    }
}
