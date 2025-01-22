package com.sourceCode.Airbnb.services.impl;

import com.sourceCode.Airbnb.dtos.HotelDto;
import com.sourceCode.Airbnb.entities.Hotel;
import com.sourceCode.Airbnb.exceptions.ResourceNotFoundException;
import com.sourceCode.Airbnb.repositories.HotelRepository;
import com.sourceCode.Airbnb.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {

        log.info("creating new hotel with name : {}",hotelDto.getName());
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setActive(false);
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
        return modelMapper.map(hotel,HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hoteldto) {
        log.info("updating the hotel with ID : {}",id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not found with ID :"+id));
        modelMapper.map(hoteldto , hotel);
        hotel.setId(id);
        hotel = hotelRepository.save(hotel);

        return modelMapper.map(hotel , HotelDto.class);
    }

    @Override
    public void deleteHotelById(Long id) {
        log.info("deleting the hotel with ID : {}",id);
        boolean exists = hotelRepository.existsById(id);
        if(!exists) throw new ResourceNotFoundException("Hotel Not found with ID :"+id);

        hotelRepository.deleteById(id);
        //need to do delete the future inventories
    }

    @Override
    public void activateHotel(Long hotelId) {
        log.info("activating the hotel with ID : {}",hotelId);
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not found with ID :"+hotelId));

        hotel.setActive(true);
        // TODO : create inventory for future bookings

    }
}
