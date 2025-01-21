package com.sourceCode.Airbnb.services;

import com.sourceCode.Airbnb.dtos.HotelDto;
import com.sourceCode.Airbnb.entities.Hotel;

public interface HotelService {

    HotelDto createNewHotel(HotelDto hotelDto);

    HotelDto getHotelById(Long id);
}
