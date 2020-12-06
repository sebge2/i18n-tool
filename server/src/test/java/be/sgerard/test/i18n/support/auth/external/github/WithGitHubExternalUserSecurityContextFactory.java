package be.sgerard.test.i18n.support.auth.external.github;

import be.sgerard.i18n.model.security.auth.external.ExternalAuthSystem;
import be.sgerard.i18n.model.security.auth.external.ExternalUserDetails;
import be.sgerard.i18n.model.user.ExternalUser;
import be.sgerard.i18n.model.user.dto.UserPatchDto;
import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.service.security.UserRole;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.security.auth.AuthenticationUtils;
import be.sgerard.i18n.service.user.UserManager;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toSet;

/**
 * @author Sebastien Gerard
 */
public class WithGitHubExternalUserSecurityContextFactory implements WithSecurityContextFactory<WithGitHubExternalUser> {

    private final UserManager userManager;
    private final AuthenticationUserManager authenticationUserManager;

    public WithGitHubExternalUserSecurityContextFactory(UserManager userManager, AuthenticationUserManager authenticationUserManager) {
        this.userManager = userManager;
        this.authenticationUserManager = authenticationUserManager;
    }

    @Override
    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    public SecurityContext createSecurityContext(WithGitHubExternalUser user) {
        final ExternalUserEntity userEntity = createUser(user);

        final ExternalUserDetails userDetails = new ExternalUserDetails(userEntity, emptyMap(), user.token());

        final SecurityContext securityContext = authenticationUserManager
                .createUser(userDetails)
                .map(authenticatedUser -> AuthenticationUtils.createAuthentication(userDetails, authenticatedUser))
                .map(SecurityContextImpl::new)
                .block();

        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext));

        return securityContext;
    }

    private ExternalUserEntity createUser(WithGitHubExternalUser user) {
        final Collection<UserRole> roles = Stream.of(user.roles()).filter(StringUtils::hasText).map(UserRole::valueOf).collect(toSet());

        return userManager
                .createOrUpdate(
                        ExternalUser.builder()
                                .username(user.username())
                                .displayName(user.displayName())
                                .email(user.email())
                                .authorized(user.authorized())
                                .externalId(user.username())
                                .authSystem(ExternalAuthSystem.OAUTH_GITHUB)
                                .build()
                )
                .flatMap(userEntity -> userManager.update(userEntity.getId(), UserPatchDto.builder().roles(roles).build()))
                .map(ExternalUserEntity.class::cast)
                .block();
    }
}
