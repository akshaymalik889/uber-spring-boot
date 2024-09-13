package com.project.uber.uberApp.strategies;

import com.project.uber.uberApp.entities.Payment;

public interface PaymentStrategy {

    //Commission of UBER PLATFORM
    Double PLATFORM_COMMISSION = 0.3;

    void processPayment(Payment payment);
}
