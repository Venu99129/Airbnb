package com.sourceCode.Airbnb.services.impl;

import com.sourceCode.Airbnb.entities.User;
import com.sourceCode.Airbnb.exceptions.ResourceNotFoundException;
import com.sourceCode.Airbnb.repositories.UserRepository;
import com.sourceCode.Airbnb.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService , UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not Found with this id: "+id));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()-> new BadCredentialsException("given email: "+username+" does not exists!"));
    }
}
