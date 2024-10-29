package com.treizer.spring_security_app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.treizer.spring_security_app.controllers.DTOs.AuthCreateUserDto;
import com.treizer.spring_security_app.controllers.DTOs.AuthLoginRequestDto;
import com.treizer.spring_security_app.controllers.DTOs.AuthResponseDto;
import com.treizer.spring_security_app.persistence.entities.RoleEntity;
import com.treizer.spring_security_app.persistence.entities.UserEntity;
import com.treizer.spring_security_app.persistence.repositories.IRoleRepository;
import com.treizer.spring_security_app.persistence.repositories.IUserRepository;
import com.treizer.spring_security_app.utils.JwtUtils;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "El usuario " + username + " no existe."));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        user.getRoles()
                .forEach(
                        role -> authorities.add(new SimpleGrantedAuthority(
                                "ROLE_".concat(role.getRoleName().name()))));

        user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorities
                        .add(new SimpleGrantedAuthority(permission.getName())));

        return new User(user.getUsername(),
                user.getPassword(),
                user.getIsEnabled(),
                user.getAccountNoExpired(),
                user.getCredentialNoExpired(),
                user.getAccountNoLocked(),
                authorities);
    }

    public AuthResponseDto loginUser(AuthLoginRequestDto authLoginRequestDto) {
        String username = authLoginRequestDto.username();
        String password = authLoginRequestDto.password();

        Authentication authentication = this.autenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = this.jwtUtils.createToken(authentication);

        AuthResponseDto authResponseDto = new AuthResponseDto(username, "User loged successfuly", accessToken, true);

        return authResponseDto;
    }

    public Authentication autenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Usuario o contraseña invalido/a.");
        }

        if (!this.passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Contraseña invalida");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    public AuthResponseDto createUser(AuthCreateUserDto authCreateUserDto) {
        String username = authCreateUserDto.username();
        String password = authCreateUserDto.password();
        List<String> roles = authCreateUserDto.roleRequest().roles();

        Set<RoleEntity> rolesSet = this.roleRepository.findRoleEntitiesByRoleNameIn(roles).stream()
                .collect(Collectors.toSet());

        if (rolesSet.isEmpty()) {
            throw new IllegalArgumentException("The roles specified does not exist.");
        }

        UserEntity userEntity = UserEntity.builder()
                .username(username)
                .password(this.passwordEncoder.encode(password))
                .roles(rolesSet)
                .isEnabled(true)
                .accountNoLocked(true)
                .accountNoExpired(true)
                .credentialNoExpired(true)
                .build();

        var userCreated = userRepository.save(userEntity);

        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();

        userCreated.getRoles().forEach(
                role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleName().name()))));

        userCreated.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated.getUsername(),
                userCreated.getPassword(), authorities);
        String accessToken = this.jwtUtils.createToken(authentication);

        AuthResponseDto authResponseDto = new AuthResponseDto(userCreated.getUsername(), "User Created Successfully",
                accessToken, true);

        return authResponseDto;
    }

}
