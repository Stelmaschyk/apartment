package com.apartment.apartment.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class UserUpdateRequestDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
