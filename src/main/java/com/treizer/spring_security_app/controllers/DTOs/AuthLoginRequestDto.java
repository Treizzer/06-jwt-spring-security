package com.treizer.spring_security_app.controllers.DTOs;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDto(@NotBlank String username,
        @NotBlank String password) {

}
