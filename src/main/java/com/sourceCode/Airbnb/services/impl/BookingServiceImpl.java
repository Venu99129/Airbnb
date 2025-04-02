package com.sourceCode.Airbnb.services.impl;

import com.sourceCode.Airbnb.dtos.BookingDto;
import com.sourceCode.Airbnb.dtos.BookingRequest;
import com.sourceCode.Airbnb.dtos.GuestDto;
import com.sourceCode.Airbnb.entities.*;
import com.sourceCode.Airbnb.entities.enums.BookingStatus;
import com.sourceCode.Airbnb.exceptions.ResourceNotFoundException;
import com.sourceCode.Airbnb.exceptions.UnAuthorizedException;
import com.sourceCode.Airbnb.repositories.*;
import com.sourceCode.Airbnb.services.BookingService;
import com.sourceCode.Airbnb.services.CheckOutService;
import com.stripe.model.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final GuestRepository guestRepository;
    private  final ModelMapper modelMapper;

    private final BookingRepository bookingRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final CheckOutService checkOutService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {

        log.info("Initialising booking for hotel : {}, room :,{}, date : {}-{}",bookingRequest.getHotelId(),
                bookingRequest.getRoomId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());

        Hotel hotel = hotelRepository
                .findById(bookingRequest.getHotelId())
                .orElseThrow(()-> new ResourceNotFoundException("Hotel Not found with ID :"+bookingRequest.getHotelId()));

        Room room = roomRepository
                .findById(bookingRequest.getRoomId())
                .orElseThrow(()-> new ResourceNotFoundException("Room Not found with ID :"+bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(
                bookingRequest.getRoomId(), bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(), bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;

        if(inventoryList.size() != daysCount){
            throw new IllegalStateException("rooms are not available anymore");
        }

        // reserve the room/ update the booked count of inventory

        for(Inventory inventory : inventoryList){
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        inventoryRepository.saveAll(inventoryList);

        //create the booking
        //TODO: REMOVE DUMMY USER

        //TODO: calculate dynamic amount

        Booking booking = Booking.builder()
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(room.getBasePrice())
                .build();

        booking = bookingRepository.save(booking);

        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    public BookingDto addGuestsIntoBooking(Long bookingId, List<GuestDto> guestDtoList) {
        log.info("adding guests into the booking bookingId :{}",bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException("Booking Not found with ID :"+bookingId));

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("booking has already expired");
        }

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking user "+user.getEmail()+"is UnAuthorized..!");
        }

        if(booking.getBookingStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state cannot add guests");
        }

        for(GuestDto guestDto : guestDtoList){
            Guest guest = modelMapper.map(guestDto, Guest.class);
            guest.setUser(getCurrentUser());
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);
        }
        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);



        return modelMapper.map(booking,BookingDto.class);
    }

    @Override
    @Transactional
    public String intiateBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                ()-> new ResourceNotFoundException("Booking not found with id: "+bookingId)
        );
        // checking user is belongings the same booking or not
        User user = getCurrentUser();

        if(!user.equals(booking.getUser())){
           throw new UnAuthorizedException("booking does not belong this userid:"+user.getId());
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("booking has already expired");
        }

        String sessionUrl = checkOutService.getCheckOutSession(booking,
                frontendUrl+"/payments/success",frontendUrl+"/payment/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);

        return sessionUrl;
    }

    @Override
    @Transactional
    public void captureEvent(Event event) {

    }

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
       return (User) (SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
