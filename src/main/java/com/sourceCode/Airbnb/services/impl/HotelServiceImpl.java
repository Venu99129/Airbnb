package com.sourceCode.Airbnb.services.impl;

import com.sourceCode.Airbnb.dtos.HotelDto;
import com.sourceCode.Airbnb.dtos.HotelInfoDto;
import com.sourceCode.Airbnb.dtos.RoomDto;
import com.sourceCode.Airbnb.entities.Hotel;
import com.sourceCode.Airbnb.entities.Room;
import com.sourceCode.Airbnb.entities.User;
import com.sourceCode.Airbnb.exceptions.ResourceNotFoundException;
import com.sourceCode.Airbnb.exceptions.UnAuthorizedException;
import com.sourceCode.Airbnb.repositories.HotelRepository;
import com.sourceCode.Airbnb.services.HotelService;
import com.sourceCode.Airbnb.services.InventoryService;
import com.sourceCode.Airbnb.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomService roomService;


    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {

        log.info("creating new hotel with name : {}",hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);

        hotel = hotelRepository.save(hotel);
        log.info("created a new hotel with ID : {}",hotel.getId());
        return modelMapper.map(hotel , HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("getting the hotel with ID : {}",id);

        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not found with ID :"+id));

        checkUserAccessForHotel(hotel);

        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hoteldto) {
        log.info("updating the hotel with ID : {}",id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not found with ID :"+id));

       checkUserAccessForHotel(hotel);

        modelMapper.map(hoteldto , hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);

        return modelMapper.map(hotel , HotelDto.class);
    }

    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        log.info("deleting the hotel with ID : {}",id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not found with ID :"+id));

       checkUserAccessForHotel(hotel);

        for(Room room: hotel.getRooms()){
            inventoryService.deleteInventories(room);
            roomService.deleteRoomById(room.getId());
        }
        hotelRepository.deleteById(id);

    }

    @Override
    @Transactional
    public void activateHotel(Long hotelId) {
        log.info("activating the hotel with ID : {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not found with ID :"+hotelId));

        checkUserAccessForHotel(hotel);

        hotel.setActive(true);
        hotel = hotelRepository.save(hotel);
        //assuming only do it once.
        log.info("setting inventory of hotelId : {} and rooms in hotel : {}",hotelId,hotel.getRooms());
        for(Room room : hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }
    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        log.info("getting the hotel information with ID : {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not found with ID :"+hotelId));

        Set<RoomDto> rooms = hotel.getRooms().stream()
                .map(room -> modelMapper.map(room,RoomDto.class))
                .collect(Collectors.toSet());

        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class) , rooms);
    }

    private void checkUserAccessForHotel(Hotel hotel){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorizedException("you are not Owner of the Hotel id:"+hotel.getId());
        }
    }
}
