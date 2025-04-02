package com.sourceCode.Airbnb.Security;

import com.sourceCode.Airbnb.dtos.LoginDto;
import com.sourceCode.Airbnb.dtos.SignUpRequestDto;
import com.sourceCode.Airbnb.dtos.UserDto;
import com.sourceCode.Airbnb.entities.User;
import com.sourceCode.Airbnb.entities.enums.Role;
import com.sourceCode.Airbnb.exceptions.ResourceNotFoundException;
import com.sourceCode.Airbnb.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserDto sigUp(SignUpRequestDto signUpRequestDto){

        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);
        if(user != null){
            throw new RuntimeException("User already exists with the email: "+signUpRequestDto.getEmail());
        }

        User newUser = modelMapper.map(signUpRequestDto, User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser = userRepository.save(newUser);

        return modelMapper.map(newUser, UserDto.class);
    }

    public String[] login(LoginDto loginDto){
        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(),loginDto.getPassword()
        ));

        User user = (User) authentication.getPrincipal();

        String[] arr = new String[2];
        arr[0] = jwtService.generateAccessToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public String generateAccessTokenFromRefreshToken(String refreshToken) {

        Long id = jwtService.generateUserIdFromToken(refreshToken);
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("user not found with id: "+id));
        return jwtService.generateAccessToken(user);
    }
}
