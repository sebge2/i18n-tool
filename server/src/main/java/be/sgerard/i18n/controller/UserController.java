package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.dto.UserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.service.user.UserManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller managing users.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller of users.")
public class UserController {

    private final UserManager userManager;

    public UserController(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Finds all users.
     */
    @GetMapping("/user")
    @ApiOperation(value = "Retrieves all users.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Flux<UserDto> findAll() {
        return userManager
                .findAll()
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Returns the user having the specified id.
     */
    @GetMapping(path = "/user/{id}")
    @ApiOperation(value = "Returns the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Mono<UserDto> findById(@PathVariable String id) {
        return userManager
                .findByIdOrDie(id)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Creates a new internal user.
     */
    @PostMapping(path = "/user")
    @ApiOperation(value = "Creates a new internal user.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Mono<UserDto> createUser(@RequestBody UserCreationDto creationDto) {
        return userManager
                .createUser(creationDto)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Updates the user having the specified id.
     */
    @PatchMapping(path = "/user/{id}")
    @ApiOperation(value = "Updates the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public Mono<UserDto> updateUser(@PathVariable String id,
                                    @RequestBody UserPatchDto userUpdate) {
        return userManager
                .updateUser(id, userUpdate)
                .map(entity -> UserDto.builder(entity).build());
    }

    /**
     * Deletes the user having the specified id.
     */
    @DeleteMapping(path = "/user/{id}")
    @ApiOperation(value = "Deletes the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<UserDto> deleteUserById(@PathVariable String id) {
        return userManager
                .delete(id)
                .map(entity -> UserDto.builder(entity).build());
    }
}
