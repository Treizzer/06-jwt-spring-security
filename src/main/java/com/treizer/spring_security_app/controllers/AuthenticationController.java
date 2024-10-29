package com.treizer.spring_security_app.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.treizer.spring_security_app.controllers.DTOs.AuthCreateUserDto;
import com.treizer.spring_security_app.controllers.DTOs.AuthLoginRequestDto;
import com.treizer.spring_security_app.controllers.DTOs.AuthResponseDto;
import com.treizer.spring_security_app.services.UserDetailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    private UserDetailService userDetailService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid AuthCreateUserDto authCreateUser) {
        var userDto = this.userDetailService.createUser(authCreateUser);

        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(userDto.username())
                        .toUri())
                .body(userDto);
    }

    @PostMapping("/log-in")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid AuthLoginRequestDto userRequest) {
        // return new ResponseEntity<>(this.userDetailService.loginUser(userRequest),
        // HttpStatus.OK);
        AuthResponseDto response = this.userDetailService.loginUser(userRequest);

        return ResponseEntity.ok(response);
    }

}
