package com.apartment.apartment.service.user;

import com.apartment.apartment.dto.user.UpdateRoleRequestDto;
import com.apartment.apartment.dto.user.UserRegistrationRequestDto;
import com.apartment.apartment.dto.user.UserResponseDto;
import com.apartment.apartment.dto.user.UserRoleResponseDto;
import com.apartment.apartment.dto.user.UserUpdateRequestDto;
import com.apartment.apartment.exception.EntityNotFoundException;
import com.apartment.apartment.exception.RegistrationException;
import com.apartment.apartment.mapper.UserMapper;
import com.apartment.apartment.model.Role;
import com.apartment.apartment.model.User;
import com.apartment.apartment.repository.role.RoleRepository;
import com.apartment.apartment.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Long USER_ROLE_ID = 2L;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Email %s has already registered"
                .formatted(requestDto.getEmail()));
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role defaultRole = roleRepository.getReferenceById(USER_ROLE_ID);
        user.addRole(defaultRole);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto getCurrentUser(Long userId) {
        User user = getUserById(userId);
        return userMapper.toDto(user);
    }

    @Transactional
    @Override
    public UserRoleResponseDto updateRoleByUserId(Long userId, UpdateRoleRequestDto requestDto) {
        User user = getUserById(userId);
        user.setRoles(getRolesById(requestDto.rolesId()));
        return userMapper.toDtoWithRoles(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserResponseDto updateUserInfoById(Long userId, UserUpdateRequestDto updateDto) {
        User user = getUserById(userId);
        userMapper.toUpdateDto(updateDto, user);
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    private Set<Role> getRolesById(Set<Long> rolesId) {
        return rolesId.stream()
            .map(roleRepository::getReferenceById)
            .collect(Collectors.toSet());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
            () -> new EntityNotFoundException("User with id %s wasn't found".formatted(userId)));
    }
}
