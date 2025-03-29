package com.sourceCode.Airbnb.services;

import com.sourceCode.Airbnb.entities.Hotel;
import com.sourceCode.Airbnb.entities.HotelMiniPrice;
import com.sourceCode.Airbnb.entities.Inventory;
import com.sourceCode.Airbnb.repositories.HotelMiniPriceRepository;
import com.sourceCode.Airbnb.repositories.HotelRepository;
import com.sourceCode.Airbnb.repositories.InventoryRepository;
import com.sourceCode.Airbnb.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PricingUpdateService {

    // scheduler to update the price inventory and HotelMiniPrice tables every hour
    private final HotelRepository hotelRepository;
    private final InventoryRepository inventoryRepository;
    private final HotelMiniPriceRepository hotelMiniPriceRepository;
    private final PricingService pricingService;

//    @Scheduled(cron = "*/5 * * * * *")
    public void updatePrice(){
        int page = 0;
        int batchSize = 100;

        while(true){
            Page<Hotel> hotelsPage = hotelRepository.findAll(PageRequest.of(page,batchSize));
            if(hotelsPage.isEmpty()){
                break;
            }
            hotelsPage.getContent().forEach(this::updateHotelPrices);

            page++;
        }
    }

    private void updateHotelPrices(Hotel hotel) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = inventoryRepository.findByHotelAndDateBetween(hotel,startDate,endDate);

        updateInventoryPrices(inventoryList);

        updateHotelMiniPrice(hotel,inventoryList,startDate,endDate);
    }

    private void updateHotelMiniPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {

        //compute minimum price per day for the hotel
        Map<LocalDate,BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice , Collectors.minBy(Comparator.naturalOrder()))
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey , e -> e.getValue().orElse(BigDecimal.ZERO)));

        List<HotelMiniPrice> hotelMiniPrices = new ArrayList<>();
        dailyMinPrices.forEach((date,price)->{
            HotelMiniPrice hotelPrice = hotelMiniPriceRepository.findByHotelAndDate(hotel,date)
                    .orElse(new HotelMiniPrice(hotel,date));
            hotelPrice.setPrice(price);
            hotelMiniPrices.add(hotelPrice);
        });

        //save all HotelPrice entities in bulk
        hotelMiniPriceRepository.saveAll(hotelMiniPrices);
    }

    private void updateInventoryPrices(List<Inventory> inventoryList) {
        inventoryList.forEach(inventory -> {
            BigDecimal dynamicPrice = pricingService.calculateDynamicPricing(inventory);
            inventory.setPrice(dynamicPrice);
        });
        inventoryRepository.saveAll(inventoryList);

    }
}
