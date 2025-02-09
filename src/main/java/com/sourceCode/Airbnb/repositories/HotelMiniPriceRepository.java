package com.sourceCode.Airbnb.repositories;

import com.sourceCode.Airbnb.dtos.HotelPriceDto;
import com.sourceCode.Airbnb.entities.Hotel;
import com.sourceCode.Airbnb.entities.HotelMiniPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HotelMiniPriceRepository extends JpaRepository<HotelMiniPrice,Long> {

    @Query("""
            SELECT new com.sourceCode.Airbnb.dtos.HotelPriceDto(i.hotel, MIN(i.price))
            FROM HotelMiniPrice i
                WHERE i.hotel.city = :city
                AND i.date BETWEEN :startDate AND :endDate
                AND i.hotel.active = true
            GROUP BY i.hotel
            """)
    Page<HotelPriceDto> findHotelWithAvailableHotelMinPrice(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
            );

    Optional<HotelMiniPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
