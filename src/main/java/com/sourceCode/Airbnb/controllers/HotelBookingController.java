package com.sourceCode.Airbnb.controllers;

import com.sourceCode.Airbnb.dtos.BookingDto;
import com.sourceCode.Airbnb.dtos.BookingRequest;
import com.sourceCode.Airbnb.dtos.GuestDto;
import com.sourceCode.Airbnb.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class HotelBookingController {

    public final BookingService bookingService;

    @PostMapping(path = "/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping(path = "/{bookingId}/guests")
    public ResponseEntity<BookingDto> addGuestsIntoBooking(@PathVariable Long bookingId,
                                                           @RequestBody List<GuestDto> guestDtoList){
        return ResponseEntity.ok(bookingService.addGuestsIntoBooking(bookingId,guestDtoList));
    }

    @PostMapping(path = "/{bookingId}/payments")
    public ResponseEntity<Map<String,String>> intiatePayment(@PathVariable Long bookingId){
        String sessionURL = bookingService.intiateBooking(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl",sessionURL));
    }
}
