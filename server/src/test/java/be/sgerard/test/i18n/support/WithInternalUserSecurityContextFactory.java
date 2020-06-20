package be.sgerard.test.i18n.support;

import be.sgerard.i18n.model.security.auth.InternalAuthenticatedUser;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Sebastien Gerard
 */
public class WithInternalUserSecurityContextFactory implements WithSecurityContextFactory<WithInternalUser> {

    @Override
    public SecurityContext createSecurityContext(WithInternalUser user) {
        final String username = user.username();

        final Collection<GrantedAuthority> authorities = new HashSet<>();
        for (String role : user.roles()) {
            if (StringUtils.hasText(role)) {
                Assert.isTrue(!role.startsWith("ROLE_"), "roles cannot start with ROLE_. Got " + role + ".");

                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }

        final SecurityContext context = SecurityContextHolder.createEmptyContext();

        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new InternalAuthenticatedUser(
                        username,
                        UserDto.builder()
                                .username(username)
                                .type(UserDto.Type.INTERNAL)
                                .build(),
                        null,
                        null,
                        authorities
                ),
                null,
                authorities
        );

        context.setAuthentication(authentication);

        return context;
    }
}
