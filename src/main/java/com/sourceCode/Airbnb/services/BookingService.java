package com.sourceCode.Airbnb.services;

import com.sourceCode.Airbnb.dtos.BookingDto;
import com.sourceCode.Airbnb.dtos.BookingRequest;
import com.sourceCode.Airbnb.dtos.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuestsIntoBooking(Long bookingId, List<GuestDto> guestDtoList);
}
