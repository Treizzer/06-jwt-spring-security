package com.treizer.spring_security_app.controllers.DTOs;

import java.util.List;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Size;

@Validated
public record AuthCreateRoleRequestDto(
        @Size(max = 3, message = "The user cannot have more than 3 roels") List<String> roles) {

}
