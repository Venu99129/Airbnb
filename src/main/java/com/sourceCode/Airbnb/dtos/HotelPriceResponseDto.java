package com.sourceCode.Airbnb.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceResponseDto {

    // this class using to expose only the hotelDto instead of hotel class rooms

    private HotelDto hotel;
    private BigDecimal price;
}
