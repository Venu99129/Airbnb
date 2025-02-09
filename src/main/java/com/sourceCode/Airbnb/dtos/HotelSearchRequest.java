package com.sourceCode.Airbnb.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class HotelSearchRequest {

    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer roomCount;

    private Integer page =0;
    private Integer size =10;
}
