package be.sgerard.poc.githuboauth.service.auth;

import be.sgerard.poc.githuboauth.model.auth.UserDto;
import org.springframework.security.access.AccessDeniedException;

/**
 * @author Sebastien Gerard
 */
public interface AuthenticationManager {

    UserDto getCurrentUser() throws AccessDeniedException;

    String getAuthToken() throws AccessDeniedException;

    boolean isAuthenticated();
}
