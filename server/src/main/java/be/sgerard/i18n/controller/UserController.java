package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.dto.UserCreationDto;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.security.user.dto.UserPatchDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.user.UserManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

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
     * Retrieves all users.
     */
    @GetMapping("/user")
    @ApiOperation(value = "Retrieves all users.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Collection<UserDto> getAllUsers() {
        return userManager.getAllUsers().stream().map(entity -> UserDto.builder(entity).build()).collect(toList());
    }

    /**
     * Returns the user having the specified id.
     */
    @GetMapping(path = "/user/{id}")
    @ApiOperation(value = "Returns the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public UserDto getUserById(@PathVariable String id) {
        return userManager.getUserById(id)
                .map(entity -> UserDto.builder(entity).build())
                .orElseThrow(() -> ResourceNotFoundException.userNotFoundException(id));
    }

    /**
     * Creates a new internal user.
     */
    @PostMapping(path = "/user")
    @ApiOperation(value = "Creates a new internal user.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto createUser(@RequestBody UserCreationDto creationDto) {
        return UserDto.builder(userManager.createUser(creationDto)).build();
    }

    /**
     * Updates the user having the specified id.
     */
    @PatchMapping(path = "/user/{id}")
    @ApiOperation(value = "Updates the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUser(@PathVariable String id,
                              @RequestBody UserPatchDto userUpdate) {
        return UserDto.builder(userManager.updateUser(id, userUpdate)).build();
    }

    /**
     * Deletes the user having the specified id.
     */
    @DeleteMapping(path = "/user/{id}")
    @ApiOperation(value = "Deletes the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable String id) {
        userManager.deleteUserById(id);
    }
}
