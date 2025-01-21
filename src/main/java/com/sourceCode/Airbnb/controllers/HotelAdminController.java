package com.sourceCode.Airbnb.controllers;

import com.sourceCode.Airbnb.dtos.HotelDto;
import com.sourceCode.Airbnb.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelAdminController {

    private final HotelService hotelService;

    @PostMapping
    public ResponseEntity<HotelDto> createNewHotel(@RequestBody HotelDto hotelDto){
        log.info("Attempting to create a new hotel with name: {}",hotelDto.getName());
        HotelDto hotel = hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotel , HttpStatus.CREATED);
    }

    @GetMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId){
        HotelDto hotel = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotel);
    }
}
