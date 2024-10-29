package com.treizer.spring_security_app.controllers.DTOs;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "username", "message", "jwt", "status" })
public record AuthResponseDto(String username,
        String message,
        String jwt,
        Boolean status) {

}
