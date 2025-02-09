package com.sourceCode.Airbnb.strategy;

import com.sourceCode.Airbnb.entities.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {

    BigDecimal calculatingPrice(Inventory inventory);
}
