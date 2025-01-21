package com.sourceCode.Airbnb.repositories;

import com.sourceCode.Airbnb.entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {
}
