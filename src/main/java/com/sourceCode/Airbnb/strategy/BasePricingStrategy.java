package com.sourceCode.Airbnb.strategy;

import com.sourceCode.Airbnb.entities.Inventory;
import java.math.BigDecimal;

public class BasePricingStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculatingPrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
