package com.sourceCode.Airbnb.services;

import com.sourceCode.Airbnb.dtos.HotelPriceDto;
import com.sourceCode.Airbnb.dtos.HotelPriceResponseDto;
import com.sourceCode.Airbnb.dtos.HotelSearchRequest;
import com.sourceCode.Airbnb.entities.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteInventories(Room room);

    Page<HotelPriceResponseDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
