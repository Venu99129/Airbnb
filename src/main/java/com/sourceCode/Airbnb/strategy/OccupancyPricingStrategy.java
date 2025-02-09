package com.sourceCode.Airbnb.strategy;

import com.sourceCode.Airbnb.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatingPrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatingPrice(inventory);
        double occupancyRate = (double) (inventory.getBookedCount() / inventory.getTotalCount());

        if(occupancyRate > 0.8){
            price = price.multiply(BigDecimal.valueOf(1.2));
        }

        return price;
    }
}
