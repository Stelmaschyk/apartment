package com.apartment.apartment.dto.user;

import java.util.Set;

public record UserRoleResponseDto(String email, Set<Long> roleIds) {
}
