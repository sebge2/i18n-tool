package be.sgerard.test.i18n.support;

import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
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

    @Override
    public SecurityContext createSecurityContext(WithInternalUser user) {
        final String username = user.username();
        final Collection<UserRole> roles = Stream.of(user.roles()).filter(StringUtils::hasText).map(UserRole::valueOf).collect(toSet());

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new InternalAuthenticatedUser(
                        username,
                        UserDto.builder()
                                .id("fake-user-id")
                                .email(username + "@acme.com")
                                .username(username)
                                .type(UserDto.Type.INTERNAL)
                                .roles(roles)
                                .build(),
                        null,
                        roles
                ),
                null,
                roles.stream().map(UserRole::toAuthority).collect(toSet())
        );

        final SecurityContext context = new SecurityContextImpl(authentication);

        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context));

        return context;
    }
}
