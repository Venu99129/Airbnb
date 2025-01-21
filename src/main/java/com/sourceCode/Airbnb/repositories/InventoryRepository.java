package com.sourceCode.Airbnb.repositories;

import com.sourceCode.Airbnb.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory , Long> {
}
