package com.sourceCode.Airbnb.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HotelMiniPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id" , nullable = false)
    private Hotel hotel;

    @Column(nullable = false , precision = 10 , scale = 2)
    private BigDecimal price; // cheapest room price on a particular date

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDate updatedAt;


    public HotelMiniPrice(Hotel hotel, LocalDate date) {
        this.date=date;
        this.hotel = hotel;
    }
}
