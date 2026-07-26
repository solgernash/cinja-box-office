package com.cinema.service;

import com.cinema.model.PaymentCard;
import org.springframework.stereotype.Service;

/*
 * Simulated payment processing. Sprint 3 only shows a payment-page mockup, so
 * this is a placeholder that always "succeeds"; real payment processing is a
 * later deliverable (and would be orchestrated by the CheckoutFacade).
 */
@Service
public class PaymentService {

    public boolean processPayment(PaymentCard card) {
        // No real charge is made for this sprint.
        return card != null;
    }
}
