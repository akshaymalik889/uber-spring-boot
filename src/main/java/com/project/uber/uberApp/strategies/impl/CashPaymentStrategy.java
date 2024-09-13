package com.project.uber.uberApp.strategies.impl;

import com.project.uber.uberApp.entities.Driver;
import com.project.uber.uberApp.entities.Payment;
import com.project.uber.uberApp.entities.enums.PaymentStatus;
import com.project.uber.uberApp.entities.enums.TransactionMethod;
import com.project.uber.uberApp.repositories.PaymentRepository;
import com.project.uber.uberApp.services.WalletService;
import com.project.uber.uberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//Rider to Pay Driver = 100rs
// Driver = Get 100Rs as Cash and deduct 30 Rs from driver wallet of Platform Commission

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {

        //get driver Details
        Driver driver = payment.getRide().getDriver();

        //find Platform Commission
        double platformCommission = payment.getAmount() * PLATFORM_COMMISSION;

        //deduct platformCommission from Driver Wallet and also add transaction in Wallet transaction (done in Wallet Service)
        walletService.deductMoneyFromWallet(driver.getUser(), platformCommission,
                null, payment.getRide(), TransactionMethod.RIDE);

        //update payment status to Confirmed
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}
