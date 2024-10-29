package com.treizer.spring_security_app.controllers.DTOs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AuthCreateUserDto(@NotBlank String username,
        @NotBlank String password,
        @Valid AuthCreateRoleRequestDto roleRequest) {

}
