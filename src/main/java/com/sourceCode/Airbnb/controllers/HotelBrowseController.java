package com.sourceCode.Airbnb.controllers;

import com.sourceCode.Airbnb.dtos.*;
import com.sourceCode.Airbnb.services.HotelService;
import com.sourceCode.Airbnb.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/hotels")
@RequiredArgsConstructor
public class HotelBrowseController {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping(path = "/search")
    public ResponseEntity<Page<HotelPriceResponseDto>> searchHotels(@RequestBody HotelSearchRequest hotelSearchRequest){
       Page<HotelPriceResponseDto> page =  inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @GetMapping(path = "/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
