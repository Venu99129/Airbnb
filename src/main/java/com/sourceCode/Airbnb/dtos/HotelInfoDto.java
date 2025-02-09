package com.sourceCode.Airbnb.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class HotelInfoDto {
    private HotelDto hotelDto;
    private Set<RoomDto> rooms;
}
