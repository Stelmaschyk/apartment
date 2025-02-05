package service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import com.apartment.apartment.service.user.UserServiceImpl;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import util.TestServiceDataProvider;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private static final Long TEST_USER_ID = 1L;
    private static final Long USER_ROLE_ID = 2L;
    private static final Long INCORRECT_USER_ID = 111L;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerUser_withValidRegistrationRequestDto_ShouldReturnValidResponseDto() {
        //Given
        UserRegistrationRequestDto requestDto =
            TestServiceDataProvider.createValidUserRegistrationRequestDto();
        UserResponseDto responseDto =
            TestServiceDataProvider.createValidUserResponseDto(TEST_USER_ID);
        User user = TestServiceDataProvider.createValidUser();
        Role role = TestServiceDataProvider.createValidUserRole();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(requestDto)).thenReturn(user);
        when(roleRepository.getReferenceById(USER_ROLE_ID)).thenReturn(role);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(responseDto);

        // When
        try {
            UserResponseDto actual = userService.register(requestDto);
            assertThat(actual)
                .isNotNull()
                .isEqualTo(responseDto);

            verify(userRepository).existsByEmail(requestDto.getEmail());
            verify(userMapper).toModel(requestDto);
            verify(roleRepository).getReferenceById(USER_ROLE_ID);
            verify(userRepository).save(user);
            verify(userMapper).toDto(user);
        } catch (RegistrationException e) {
            fail("RegistrationException should not have been thrown: " + e.getMessage());
        }
    }


    @Test
    void registerUser_withExistingUserEmail_ShouldReturnRegistrationException() {
        UserRegistrationRequestDto requestDto =
            TestServiceDataProvider.createValidUserRegistrationRequestDto();

        when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        RegistrationException registrationException = assertThrows(
            RegistrationException.class, () -> userService.register(requestDto));

        String expectedMessage = "Email %s has already registered"
            .formatted(requestDto.getEmail());
        String actual = registrationException.getMessage();

        assertThat(actual)
            .isNotNull()
            .isEqualTo(expectedMessage);

    }

    @Test
    void getCurrentUser_withValidUserId_shouldReturnUserResponseDto() {
        UserResponseDto responseDto =
            TestServiceDataProvider.createValidUserResponseDto(TEST_USER_ID);
        User user = TestServiceDataProvider.createValidUser();

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(responseDto);

        UserResponseDto actual = userService.getCurrentUser(TEST_USER_ID);
        assertThat(actual)
            .isNotNull()
            .isEqualTo(responseDto);
    }

    @Test
    void getCurrentUser_withInValidUserId_shouldReturnUserResponseDto() {
        when(userRepository.findById(INCORRECT_USER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(
            EntityNotFoundException.class, () -> userService.getCurrentUser(INCORRECT_USER_ID));

        String expectedMessage = "User with id %s wasn't found".formatted(INCORRECT_USER_ID);
        String actual = entityNotFoundException.getMessage();

        assertThat(actual)
            .isNotNull()
            .isEqualTo(expectedMessage);

        verify(userRepository, times(1)).findById(INCORRECT_USER_ID);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateRoleByUserId_shouldUpdateRolesAndReturnDto() {
        User user = TestServiceDataProvider.createValidUser();
        Role role = TestServiceDataProvider.createValidUserRole(USER_ROLE_ID);
        Set<Long> roleIds = Set.of(USER_ROLE_ID);

        UpdateRoleRequestDto requestDto = new UpdateRoleRequestDto(roleIds);
        UserRoleResponseDto responseDto = TestServiceDataProvider.createValidUserRoleResponseDto();
        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        when(roleRepository.getReferenceById(USER_ROLE_ID)).thenReturn(role);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDtoWithRoles(any(User.class))).thenReturn(responseDto);

        UserRoleResponseDto response = userService.updateRoleByUserId(TEST_USER_ID, requestDto);

        assertThat(response).isNotNull();
        verify(userRepository).findById(TEST_USER_ID);
        verify(roleRepository).getReferenceById(USER_ROLE_ID);
        verify(userRepository).save(user);
        verify(userMapper).toDtoWithRoles(user);
    }

    @Test
    void updateUserInfoById_shouldUpdateUserAndReturnDto() {
        User user = TestServiceDataProvider.createValidUser();
        UserUpdateRequestDto updateDto = TestServiceDataProvider.createValidUserUpdateRequestDto();
        UserResponseDto expectedResponse =
            TestServiceDataProvider.createUpdatedUserResponseDto(TEST_USER_ID);

        when(userRepository.findById(TEST_USER_ID)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).toUpdateDto(updateDto, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(expectedResponse);

        UserResponseDto actual = userService.updateUserInfoById(TEST_USER_ID, updateDto);

        assertThat(actual).isNotNull();
        assertThat(actual.getFirstName()).isEqualTo(expectedResponse.getFirstName());
        assertThat(actual.getLastName()).isEqualTo(expectedResponse.getLastName());

        verify(userRepository).findById(TEST_USER_ID);
        verify(userMapper).toUpdateDto(updateDto, user);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }


}
