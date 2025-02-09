package com.sourceCode.Airbnb.repositories;

import com.sourceCode.Airbnb.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}