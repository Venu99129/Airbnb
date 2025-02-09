package com.sourceCode.Airbnb.services.impl;

import com.sourceCode.Airbnb.dtos.HotelDto;
import com.sourceCode.Airbnb.dtos.HotelPriceDto;
import com.sourceCode.Airbnb.dtos.HotelPriceResponseDto;
import com.sourceCode.Airbnb.dtos.HotelSearchRequest;
import com.sourceCode.Airbnb.entities.Inventory;
import com.sourceCode.Airbnb.entities.Room;
import com.sourceCode.Airbnb.repositories.HotelMiniPriceRepository;
import com.sourceCode.Airbnb.repositories.InventoryRepository;
import com.sourceCode.Airbnb.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMiniPriceRepository hotelMiniPriceRepository;
    @Override
    public void initializeRoomForAYear(Room room) {
        log.info("initializing inventory for year room ID : {}",room.getId());
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for(; today.isBefore(endDate);today = today.plusDays(1)){
            System.out.println(today);
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .surgeFactor(BigDecimal.ONE)
                    .price(room.getBasePrice())
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventoryRepository.save(inventory);
        }

    }

    @Override
    public void deleteInventories(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceResponseDto> searchHotels(HotelSearchRequest hotelSearchRequest) {
        log.info("searching hotels with startDate: {} , endDate : {}",hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate());

        Pageable pageable = PageRequest.of(hotelSearchRequest.getPage(), hotelSearchRequest.getSize());
        long dateCount = ChronoUnit.DAYS.between(
                hotelSearchRequest.getStartDate(),hotelSearchRequest.getEndDate())+1;

        Page<HotelPriceDto> hotelPageWithActualData = hotelMiniPriceRepository.findHotelWithAvailableHotelMinPrice(
                                 hotelSearchRequest.getCity(),
                                 hotelSearchRequest.getStartDate(),
                                 hotelSearchRequest.getEndDate(),
                                 pageable);

        List<HotelPriceResponseDto> content = hotelPageWithActualData.stream()
                                            .map(hotelWithData -> HotelPriceResponseDto.builder()
                                                    .hotel(modelMapper.map(hotelWithData.getHotel(), HotelDto.class))
                                                    .price(hotelWithData.getPrice()).build())
                                            .toList();

        // Pageable object for pagination (page number, page size)
        PageRequest pageableDummy = PageRequest.of(0, 10); // page 0, 3 items per page

        // Total number of elements
        long totalElements = hotelPageWithActualData.getTotalElements();

        // Create a Page object
        Page<HotelPriceResponseDto> hotelPageWithExposeData  = new PageImpl<>(content, pageableDummy, totalElements);

        return hotelPageWithExposeData;
    }
}
