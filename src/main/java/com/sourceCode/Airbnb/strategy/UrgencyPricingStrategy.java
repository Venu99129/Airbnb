package com.sourceCode.Airbnb.strategy;

import com.sourceCode.Airbnb.entities.Inventory;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@RequiredArgsConstructor
public class UrgencyPricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatingPrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatingPrice(inventory);

        LocalDate today = LocalDate.now();
        if(inventory.getDate().isBefore(today.plusDays(7))){
            price = price.multiply(BigDecimal.valueOf(1.15));
        }
        return price;
    }
}
