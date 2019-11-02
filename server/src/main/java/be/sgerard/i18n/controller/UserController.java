package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.security.user.UserCreationDto;
import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.model.security.user.UserUpdateDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.security.user.UserManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

/**
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

    @GetMapping("/user")
    @ApiOperation(value = "Retrieves all users.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public Collection<UserDto> loadAllUsers() {
        return userManager.loadAllUsers().stream().map(entity -> UserDto.builder(entity).build()).collect(toList());
    }

    @GetMapping(path = "/user/{id}")
    @ApiOperation(value = "Returns the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional(readOnly = true)
    public UserDto getUserById(@PathVariable String id) {
        return userManager.getUserById(id)
                .map(entity -> UserDto.builder(entity).build())
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @PostMapping(path = "/user")
    @ApiOperation(value = "Creates a new internal user.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto createUser(@RequestBody UserCreationDto creationDto) {
        return UserDto.builder(userManager.createUser(creationDto)).build();
    }

    @PatchMapping(path = "/user/{id}")
    @ApiOperation(value = "Updates the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserDto updateUser(@PathVariable String id, @RequestBody UserUpdateDto userUpdate) {
        return UserDto.builder(userManager.updateUser(id, userUpdate)).build();
    }

    @DeleteMapping(path = "/user/{id}")
    @ApiOperation(value = "Deletes the user having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUserById(@PathVariable String id) {
        userManager.deleteUserById(id);
    }
}
