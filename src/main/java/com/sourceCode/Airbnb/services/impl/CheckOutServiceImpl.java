package com.sourceCode.Airbnb.services.impl;

import com.sourceCode.Airbnb.entities.Booking;
import com.sourceCode.Airbnb.entities.User;
import com.sourceCode.Airbnb.repositories.BookingRepository;
import com.sourceCode.Airbnb.services.CheckOutService;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckOutServiceImpl implements CheckOutService {

    private final BookingRepository bookingRepository;

    @Override
    public String getCheckOutSession(Booking booking, String successUrl, String failureUrl) {
        log.info("creating session for booking with id :{}",booking.getId());

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            CustomerCreateParams customerCreateParams = CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();
            Customer customer = Customer.create(customerCreateParams);

            SessionCreateParams sessionParams = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("inr")
                                                    .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(booking.getHotel().getName()+"  "+booking.getRoom().getType())
                                                                    .setDescription("Booking ID : "+booking.getId())
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(sessionParams);

            booking.setPaymentSessionId(session.getId());
            bookingRepository.save(booking);

            log.info("Session created successfully for the booking withId : {}",booking.getId());
            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }
}
