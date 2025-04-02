package com.sourceCode.Airbnb.services;

import com.sourceCode.Airbnb.entities.Booking;

public interface CheckOutService {

    String getCheckOutSession(Booking booking, String successUrl, String failureUrl);
}
