package be.sgerard.test.i18n.support;

import be.sgerard.i18n.model.security.auth.internal.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.service.security.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toSet;

/**
 * @author Sebastien Gerard
 */
public class WithInternalUserSecurityContextFactory implements WithSecurityContextFactory<WithInternalUser> {

    @Override
    public SecurityContext createSecurityContext(WithInternalUser user) {
        final String username = user.username();
        final Collection<UserRole> roles = Stream.of(user.roles()).filter(StringUtils::hasText).map(UserRole::valueOf).collect(toSet());

        final SecurityContext context = SecurityContextHolder.createEmptyContext();

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new InternalAuthenticatedUser(
                        username,
                        UserDto.builder()
                                .username(username)
                                .type(UserDto.Type.INTERNAL)
                                .build(),
                        null,
                        roles,
                        emptyList()
                ),
                null,
                roles.stream().map(UserRole::toAuthority).collect(toSet())
        );

        context.setAuthentication(authentication);

        return context;
    }
}
