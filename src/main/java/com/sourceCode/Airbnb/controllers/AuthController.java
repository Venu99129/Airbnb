package com.sourceCode.Airbnb.controllers;

import com.sourceCode.Airbnb.Security.AuthService;
import com.sourceCode.Airbnb.dtos.LoginDto;
import com.sourceCode.Airbnb.dtos.LoginResponseDto;
import com.sourceCode.Airbnb.dtos.SignUpRequestDto;
import com.sourceCode.Airbnb.dtos.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto){
        return new ResponseEntity<>(authService.sigUp(signUpRequestDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto , HttpServletRequest request , HttpServletResponse response){
        String[] tokens = authService.login(loginDto);

        Cookie cookie = new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }
}
