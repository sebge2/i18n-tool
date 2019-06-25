package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.model.auth.AuthenticationDto;
import org.springframework.security.access.AccessDeniedException;

/**
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    AuthenticationDto getCurrentAuth() throws AccessDeniedException;

    boolean isAuthenticated();
}
