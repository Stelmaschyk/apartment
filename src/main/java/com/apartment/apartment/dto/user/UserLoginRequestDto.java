package com.apartment.apartment.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class UserLoginRequestDto {
    @NotBlank
    @Size(min = 5, max = 30)
    private String email;
    @NotBlank
    @Size(min = 8, max = 30)
    private String password;
}
