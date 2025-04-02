package com.sourceCode.Airbnb.services;

import com.sourceCode.Airbnb.dtos.RoomDto;

import java.util.List;
import java.util.Set;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);

    Set<RoomDto> getAllRoomsInHotel(Long hotelId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long roomId);
}
