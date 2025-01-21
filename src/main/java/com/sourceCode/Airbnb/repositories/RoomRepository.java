package com.sourceCode.Airbnb.repositories;

import com.sourceCode.Airbnb.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room , Long> {
}
