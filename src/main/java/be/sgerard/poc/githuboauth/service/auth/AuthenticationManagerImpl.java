package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.auth.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

/**
 * @author Sebastien Gerard
 */
@Service
public class AuthenticationManagerImpl implements AuthenticationManager {

    public AuthenticationManagerImpl() {
    }

    @Override
    public Authentication getCurrentAuth() {
        final org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2Authentication) {
            final OAuth2Authentication oauthAuthentication = (OAuth2Authentication) authentication;
            final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) oauthAuthentication.getDetails();

            @SuppressWarnings("unchecked") final Map<String, String> profileDetails = (Map<String, String>) oauthAuthentication.getUserAuthentication().getDetails();

            return new Authentication(details.getTokenValue(), Objects.toString(oauthAuthentication.getPrincipal(), null), profileDetails.get("email"));
        } else {
            throw new AccessDeniedException("Cannot access to git.");
        }
    }
}
