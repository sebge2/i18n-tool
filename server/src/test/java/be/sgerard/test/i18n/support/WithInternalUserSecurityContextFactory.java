package be.sgerard.test.i18n.support;

import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.InternalUserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public WithInternalUserSecurityContextFactory(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public SecurityContext createSecurityContext(WithInternalUser user) {
        final InternalUserEntity userEntity = createUser(user);

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new InternalAuthenticatedUser(
                        userEntity.getUsername(),
                        UserDto.builder(userEntity).build(),
                        null,
                        userEntity.getRoles()
                ),
                null,
                userEntity.getRoles().stream().map(UserRole::toAuthority).collect(toSet())
        );

        final SecurityContext context = new SecurityContextImpl(authentication);

        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context));

        return context;
    }

    private InternalUserEntity createUser(WithInternalUser user) {
        final String username = user.username();
        final Collection<UserRole> roles = Stream.of(user.roles()).filter(StringUtils::hasText).map(UserRole::valueOf).collect(toSet());

        return userManager
                .createUser(InternalUserCreationDto.builder()
                        .username(username)
                        .roles(roles)
                        .password("")
                        .email(username + "@acme.com")
                        .build()
                )
                .block();
    }
}
