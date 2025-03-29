package com.sourceCode.Airbnb.services.impl;

import com.sourceCode.Airbnb.entities.User;
import com.sourceCode.Airbnb.exceptions.ResourceNotFoundException;
import com.sourceCode.Airbnb.repositories.UserRepository;
import com.sourceCode.Airbnb.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not Found with this id: "+id));
    }
}
