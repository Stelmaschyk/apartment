package com.apartment.apartment.controller;

import com.apartment.apartment.dto.user.UpdateRoleRequestDto;
import com.apartment.apartment.dto.user.UserResponseDto;
import com.apartment.apartment.dto.user.UserRoleResponseDto;
import com.apartment.apartment.dto.user.UserUpdateRequestDto;
import com.apartment.apartment.model.User;
import com.apartment.apartment.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User profile management",
        description = "Endpoints for user profile management")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Retrieve current user information",
            description = "Returns details of the authenticated user")
    @GetMapping("/me")
    public UserResponseDto getUser(@AuthenticationPrincipal User user) {
        return userService.getCurrentUser(user.getId());
    }

    @Operation(summary = "Update user`s role", description = "Update user`s role by Id."
            + "Role by default = ROLE_USER")
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public UserRoleResponseDto updateRoleById(
            @PathVariable Long id, @RequestBody @Valid UpdateRoleRequestDto requestDto) {
        return userService.updateRoleByUserId(id, requestDto);
    }

    @Operation(summary = "Update user`s profile information",
            description = "Allows users to update their firstname and lastname")
    @PatchMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto update(@AuthenticationPrincipal User user,
                                  @RequestBody @Valid UserUpdateRequestDto requestDto) {
        return userService.updateUserInfoById(user.getId(), requestDto);
    }
}
