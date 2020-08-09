package be.sgerard.test.i18n.support;

import be.sgerard.i18n.model.security.auth.internal.InternalUserDetails;
import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * @author Sebastien Gerard
 */
public class WithInternalUserSecurityContextFactory implements WithSecurityContextFactory<WithInternalUser> {

    private final UserManager userManager;
    private final AuthenticationManager authenticationManager;

    public WithInternalUserSecurityContextFactory(UserManager userManager, AuthenticationManager authenticationManager) {
        this.userManager = userManager;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    public SecurityContext createSecurityContext(WithInternalUser user) {
        final InternalUserEntity userEntity = createUser(user);

        final SecurityContext securityContext = authenticationManager
                .createAuthentication(new InternalUserDetails(userEntity))
                .map(SecurityContextImpl::new)
                .block();

        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext));

        return securityContext;
    }

    private InternalUserEntity createUser(WithInternalUser user) {
        final String username = user.username();
        final Collection<UserRole> roles = Stream.of(user.roles()).filter(StringUtils::hasText).map(UserRole::valueOf).collect(toSet());

        return userManager
                .createUser(InternalUserCreationDto.builder()
                        .username(username)
                        .displayName(user.displayName())
                        .roles(roles)
                        .password(user.password())
                        .email(user.email())
                        .build()
                )
                .block();
    }
}
