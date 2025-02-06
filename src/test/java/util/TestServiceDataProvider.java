package util;

import com.apartment.apartment.dto.user.UserLoginRequestDto;
import com.apartment.apartment.dto.user.UserRegistrationRequestDto;
import com.apartment.apartment.dto.user.UserResponseDto;
import com.apartment.apartment.dto.user.UserRoleResponseDto;
import com.apartment.apartment.dto.user.UserUpdateRequestDto;
import com.apartment.apartment.model.Role;
import com.apartment.apartment.model.User;
import java.util.HashSet;
import java.util.Set;

public final class TestServiceDataProvider {
    public static Role createValidUserRole() {
        Role role = new Role();
        role.setRole(Role.RoleName.ROLE_USER);
        return role;
    }

    public static Role createValidUserRole(Long id) {
        Role role = new Role();
        role.setId(id);
        role.setRole(Role.RoleName.ROLE_USER);
        return role;
    }

    public static User createValidUser() {
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(2L);
        role.setRole(Role.RoleName.ROLE_USER);
        roles.add(role);

        return new User()
            .setFirstName("User")
            .setLastName("Userenko")
            .setEmail("user@gmail.com")
            .setPassword("password123")
            .setRoles(roles);
    }

    public static UserRegistrationRequestDto createValidUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto()
            .setEmail("user@gmail.com")
            .setPassword("password123")
            .setRepeatPassword("password123")
            .setFirstName("User")
            .setLastName("Userenko");
    }

    public static UserLoginRequestDto createValidUserLoginRequestDto() {
        return new UserLoginRequestDto()
            .setEmail("user@gmail.com")
            .setPassword("password123");
    }

    public static UserResponseDto createValidUserResponseDto(Long id) {
        return new UserResponseDto()
            .setId(id)
            .setFirstName("User")
            .setLastName("Userenko")
            .setEmail("user@gmail.com");
    }

    public static UserRoleResponseDto createValidUserRoleResponseDto() {
        Set<Long> roleIds = new HashSet<>();
        roleIds.add(1L);
        roleIds.add(2L);

        return new UserRoleResponseDto("user@gmail.com", roleIds);
    }

    public static UserUpdateRequestDto createValidUserUpdateRequestDto() {
        return new UserUpdateRequestDto()
            .setFirstName("UserUpdated")
            .setLastName("UserenkoUpdated");
    }

    public static UserResponseDto createUpdatedUserResponseDto(Long id) {
        return new UserResponseDto()
            .setId(id)
            .setFirstName("UserUpdated")
            .setLastName("UserenkoUpdated")
            .setEmail("user@gmail.com");
    }
}
