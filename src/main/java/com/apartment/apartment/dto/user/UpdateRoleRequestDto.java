package com.apartment.apartment.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.Set;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public record UpdateRoleRequestDto(@NotEmpty Set<@Positive Long> rolesId) {
}
