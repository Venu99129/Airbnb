package com.sourceCode.Airbnb.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sourceCode.Airbnb.entities.User;
import com.sourceCode.Airbnb.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    @JsonIgnore
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
}
