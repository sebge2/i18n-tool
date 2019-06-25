package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.model.auth.AuthenticationDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationManagerImpl implements AuthenticationManager {

    public AuthenticationManagerImpl() {
    }

    @Override
    public AuthenticationDto getCurrentAuth() {
        return doGetCurrentAuth()
            .orElseThrow(() -> new AccessDeniedException("Please authenticate."));
    }

    @Override
    public boolean isAuthenticated() {
        return doGetCurrentAuth().isPresent();
    }

    private Optional<AuthenticationDto> doGetCurrentAuth() {
        final org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2Authentication) {
            final OAuth2Authentication oauthAuthentication = (OAuth2Authentication) authentication;
            final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) oauthAuthentication.getDetails();

            @SuppressWarnings("unchecked") final Map<String, String> profileDetails = (Map<String, String>) oauthAuthentication.getUserAuthentication().getDetails();

            return Optional.of(
                new AuthenticationDto(details.getTokenValue(), Objects.toString(oauthAuthentication.getPrincipal(), null), profileDetails.get("email"))
            );
        } else {
            return Optional.empty();
        }
    }
}
