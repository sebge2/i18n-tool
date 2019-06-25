package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.auth.Authentication;
import org.springframework.security.access.AccessDeniedException;

/**
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    Authentication getCurrentAuth() throws AccessDeniedException;
}
