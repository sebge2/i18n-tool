package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.model.auth.UserDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final OAuth2ClientContext context;

    public AuthenticationManagerImpl(OAuth2ClientContext context) {
        this.context = context;
    }

    @Override
    public UserDto getCurrentUser() {
        return doGetCurrentAuth()
                .orElseThrow(() -> new AccessDeniedException("Please authenticate."));
    }

    @Override
    public String getAuthToken() throws AccessDeniedException {
        return context.getAccessToken().getValue();
    }

    @Override
    public boolean isAuthenticated() {
        return (context.getAccessToken() != null) && context.getAccessToken().isExpired();
    }

    private Optional<UserDto> doGetCurrentAuth() {
        final org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2Authentication) {
            return Optional.of((UserDto) authentication.getPrincipal());
        } else {
            return Optional.empty();
        }
    }
}
