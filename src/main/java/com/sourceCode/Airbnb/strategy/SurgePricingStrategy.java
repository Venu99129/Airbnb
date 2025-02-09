package com.sourceCode.Airbnb.strategy;

import com.sourceCode.Airbnb.entities.Inventory;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy{

    private final PricingStrategy wrapped;

    @Override
    public BigDecimal calculatingPrice(Inventory inventory) {

        BigDecimal price = wrapped.calculatingPrice(inventory);
        return price.multiply(inventory.getSurgeFactor());
    }
}
