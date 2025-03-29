package com.sourceCode.Airbnb.dtos;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class SignUpRequestDto {

    @Email
    private String email;

    private String password;

    private String name;
}
