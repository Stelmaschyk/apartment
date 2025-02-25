package com.apartment.apartment.service.user;

import com.apartment.apartment.dto.user.UpdateRoleRequestDto;
import com.apartment.apartment.dto.user.UserRegistrationRequestDto;
import com.apartment.apartment.dto.user.UserResponseDto;
import com.apartment.apartment.dto.user.UserRoleResponseDto;
import com.apartment.apartment.dto.user.UserUpdateRequestDto;
import com.apartment.apartment.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto userRegistrationRequestDto)
            throws RegistrationException;

    UserResponseDto getCurrentUser(Long userId);

    UserRoleResponseDto updateRoleByUserId(Long userId, UpdateRoleRequestDto requestDto);

    UserResponseDto updateUserInfoById(Long userId, UserUpdateRequestDto requestDto);
}
