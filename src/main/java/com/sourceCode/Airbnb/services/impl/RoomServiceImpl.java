package com.sourceCode.Airbnb.services.impl;

import com.sourceCode.Airbnb.dtos.RoomDto;
import com.sourceCode.Airbnb.entities.Hotel;
import com.sourceCode.Airbnb.entities.Room;
import com.sourceCode.Airbnb.entities.User;
import com.sourceCode.Airbnb.exceptions.ResourceNotFoundException;
import com.sourceCode.Airbnb.exceptions.UnAuthorizedException;
import com.sourceCode.Airbnb.repositories.HotelRepository;
import com.sourceCode.Airbnb.repositories.RoomRepository;
import com.sourceCode.Airbnb.services.InventoryService;
import com.sourceCode.Airbnb.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final ModelMapper modelMapper;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;

    @Override
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {
        log.info("creating a new room in hotel with hotelId : {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel Not found with ID :"+hotelId));

        checkUserAccessForRoom(hotel);

        Room room = modelMapper.map(roomDto , Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, RoomDto.class);
    }

    @Override
    public Set<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("getting all rooms in a hotel with hotelId : {}",hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not found with ID :"+hotelId));

        checkUserAccessForRoom(hotel);

        if(hotel.getRooms().isEmpty()) throw new ResourceNotFoundException("Rooms are not found with this hotelId :"+hotelId);

        return hotel.getRooms()
                .stream().map(room -> modelMapper.map(room , RoomDto.class)).collect(Collectors.toSet());
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("getting the room with roomId : {}",roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room Not found with ID :"+roomId));

        checkUserAccessForRoom(room);

        return modelMapper.map(room , RoomDto.class);
    }

    @Override
    public void deleteRoomById(Long roomId) {
        log.info("deleting the room with roomId : {}",roomId);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room Not found with ID :"+roomId));

        checkUserAccessForRoom(room);

        roomRepository.deleteById(roomId);
        inventoryService.deleteInventories(room);
    }

    private void checkUserAccessForRoom(Hotel hotel){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorizedException("you are not Owner of the Hotel id:"+hotel.getId());
        }
    }
    private void checkUserAccessForRoom(Room room){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(room.getHotel().getOwner())){
            throw new UnAuthorizedException("you are not Owner of the Room id:"+room.getId());
        }
    }
}
