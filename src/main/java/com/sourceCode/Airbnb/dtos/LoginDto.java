package com.sourceCode.Airbnb.dtos;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginDto {

    @Email
    private String email;
    private String password;
}
