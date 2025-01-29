package com.apartment.apartment.mapper;

import com.apartment.apartment.config.MapperConfig;
import com.apartment.apartment.dto.user.UserRegistrationRequestDto;
import com.apartment.apartment.dto.user.UserResponseDto;
import com.apartment.apartment.dto.user.UserRoleResponseDto;
import com.apartment.apartment.dto.user.UserUpdateRequestDto;
import com.apartment.apartment.model.Role;
import com.apartment.apartment.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(config = MapperConfig.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "email", target = "email")
    UserResponseDto toDto(User user);

    void toUpdateDto(UserUpdateRequestDto requestDto, @MappingTarget User user);

    User toModel(UserRegistrationRequestDto userRegistrationRequestDto);

    @Mapping(source = "roles", target = "roleIds")
    UserRoleResponseDto toDtoWithRoles(User user);

    default Long roleToId(Role role) {
        return role.getId();
    }
}
