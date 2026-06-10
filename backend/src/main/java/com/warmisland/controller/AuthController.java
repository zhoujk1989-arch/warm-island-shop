package com.warmisland.controller;

import com.warmisland.dto.LoginRequest;
import com.warmisland.dto.LoginResponse;
import com.warmisland.dto.Result;
import com.warmisland.entity.User;
import com.warmisland.security.JwtTokenProvider;
import com.warmisland.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication);

        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        return Result.success(new LoginResponse(jwt, loginRequest.getUsername(), role));
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody LoginRequest loginRequest) {
        if (userService.findByUsername(loginRequest.getUsername()) != null) {
            return Result.error(400, "用户名已存在");
        }

        User user = new User();
        user.setUsername(loginRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(loginRequest.getPassword()));
        user.setRole("ADMIN");

        userService.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String jwt = tokenProvider.generateToken(authentication);
        return Result.success(new LoginResponse(jwt, user.getUsername(), user.getRole()));
    }
}
