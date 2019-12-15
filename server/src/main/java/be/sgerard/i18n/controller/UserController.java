package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.dto.*;
import be.sgerard.i18n.model.security.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.security.user.persistence.InternalUserEntity;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.user.UserManager;
import be.sgerard.i18n.service.user.UserManagerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Optional;

/**
 * Controller managing users.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Tag(name = "User", description = "Controller of users.")
public class UserController {

    /**
     * Default user avatar.
     */
    public static final String UNKNOWN_USER_AVATAR = "/images/unknown-user-avatar.png";

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserManager userManager;
    private final AuthenticationUserManager authenticationUserManager;
    private final byte[] defaultUserAvatar;

    public UserController(UserManager userManager, AuthenticationUserManager authenticationUserManager) {
        this.userManager = userManager;
        this.authenticationUserManager = authenticationUserManager;
        this.defaultUserAvatar = loadDefaultUserAvatar();
    }

    /**
     * Finds all users.
     */
    @GetMapping("/user")
    @Operation(summary = "Retrieves all users.")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<UserDto> findAll() {
        return userManager
                .findAll()
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Returns the user having the specified id.
     */
    @GetMapping(path = "/user/{id}")
    @Operation(summary = "Returns the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<UserDto> findById(@PathVariable String id) {
        return userManager
                .findByIdOrDie(id)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Returns the current user.
     */
    @GetMapping(path = "/user/current")
    @Operation(summary = "Returns the current user.")
    public Mono<UserDto> getCurrent() {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .flatMap(authenticatedUser -> userManager.findByIdOrDie(authenticatedUser.getUserId()))
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Creates a new internal user.
     */
    @PostMapping(path = "/user")
    @Operation(summary = "Creates a new internal user.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Mono<UserDto> createUser(@RequestBody InternalUserCreationDto creationDto) {
        return userManager
                .createUser(creationDto)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Updates the user having the specified id.
     */
    @PatchMapping(path = "/user/{id}")
    @Operation(summary = "Updates the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Mono<UserDto> updateUser(@PathVariable String id,
                                    @RequestBody UserPatchDto patch) {
        return userManager
                .update(id, patch)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Updates the current authenticated user.
     */
    @PatchMapping(path = "/user/current")
    @Operation(summary = "Updates the current authenticated user.")
    @Transactional
    public Mono<UserDto> updateCurrentUser(@RequestBody CurrentUserPatchDto patch) {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .flatMap(authenticatedUser -> userManager.updateCurrent(authenticatedUser.getUserId(), patch))
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Updates the current user's password.
     */
    @PutMapping(path = "/user/current/password")
    @Operation(summary = "Updates the password of the current authenticated user.")
    @Transactional
    public Mono<UserDto> updateCurrentUserPassword(@RequestBody CurrentUserPasswordUpdateDto update) {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .flatMap(authenticatedUser -> userManager.updateCurrentPassword(authenticatedUser.getUserId(), update))
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Updates the avatar of the current internal user.
     */
    @PutMapping(path = "/user/current/avatar", consumes = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    @Operation(
            summary = "Updates the avatar of the current authenticated user.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(mediaType = MediaType.IMAGE_JPEG_VALUE, schema = @Schema(type = "string", format = "binary")),
                    @Content(mediaType = MediaType.IMAGE_PNG_VALUE, schema = @Schema(type = "string", format = "binary"))
            })
    )
    @Transactional
    public Mono<UserDto> updateUserAvatar(ServerHttpRequest request) {
        return DataBufferUtils.join(request.getBody())
                .map(DataBuffer::asInputStream)
                .flatMap(avatar ->
                        authenticationUserManager
                                .getCurrentUserOrDie()
                                .flatMap(authenticatedUser ->
                                        userManager.updateUserAvatar(
                                                authenticatedUser.getUserId(),
                                                avatar, Optional.ofNullable(request.getHeaders().getContentType()).map(MimeType::getType).orElse(null)
                                        )
                                )
                )
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Returns the avatar of the specified user.
     */
    @Bean
    public RouterFunction<ServerResponse> getUserAvatar() {
        return RouterFunctions.route(
                RequestPredicates.GET("/api/user/{id}/avatar"),
                request -> userManager
                        .findByIdOrDie(request.pathVariable("id"))
                        .flatMap(userEntity -> {
                            if (userEntity instanceof InternalUserEntity) {
                                final InternalUserEntity internalUserEntity = (InternalUserEntity) userEntity;

                                final DefaultDataBuffer buffer;
                                if (internalUserEntity.hasAvatar()) {
                                    buffer = new DefaultDataBufferFactory().wrap(internalUserEntity.getAvatar());
                                } else {
                                    buffer = new DefaultDataBufferFactory().wrap(defaultUserAvatar);
                                }

                                return ServerResponse
                                        .ok()
                                        .contentType(MediaType.IMAGE_PNG)
                                        .body(BodyInserters.fromDataBuffers(Flux.just(buffer)));
                            } else {
                                final ExternalUserEntity externalUserEntity = (ExternalUserEntity) userEntity;

                                return ServerResponse
                                        .ok()
                                        .contentType(MediaType.IMAGE_PNG)
                                        .body(
                                                WebClient.create().get().uri(externalUserEntity.getAvatarUrl()).retrieve().bodyToFlux(byte[].class),
                                                byte[].class
                                        );
                            }
                        })
        );
    }

    /**
     * Deletes the user having the specified id.
     */
    @DeleteMapping(path = "/user/{id}")
    @Operation(summary = "Deletes the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<UserDto> deleteUserById(@PathVariable String id) {
        return userManager
                .delete(id)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Loads the default user avatar.
     */
    private static byte[] loadDefaultUserAvatar() {
        try {
            return IOUtils.toByteArray(UserManagerImpl.class.getResourceAsStream(UNKNOWN_USER_AVATAR));
        } catch (IOException e) {
            logger.error("Error while loading default user avatar.", e);

            return new byte[0];
        }
    }
}
