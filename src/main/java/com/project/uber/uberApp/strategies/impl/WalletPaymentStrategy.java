package com.project.uber.uberApp.strategies.impl;


import com.project.uber.uberApp.entities.Driver;
import com.project.uber.uberApp.entities.Payment;
import com.project.uber.uberApp.entities.Rider;
import com.project.uber.uberApp.entities.enums.PaymentStatus;
import com.project.uber.uberApp.entities.enums.TransactionMethod;
import com.project.uber.uberApp.repositories.PaymentRepository;
import com.project.uber.uberApp.services.WalletService;
import com.project.uber.uberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//Rider have some amount in Wallet let Rs 500
// Ride Cost is 100rs
//then rider wallet will deduct by 100 so, 500 - 100 = 400 rs in rider wallet
//driver wallet will add by 70 rs (because deduct PlatForm Commission 30% ie 30 rs )

@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void processPayment(Payment payment) {

        //get driver Details
        Driver driver = payment.getRide().getDriver();

        //get Rider Details
        Rider rider = payment.getRide().getRider();

        // call wallet service to deduct money from rider wallet
        walletService.deductMoneyFromWallet(rider.getUser(), payment.getAmount(),
                null, payment.getRide(), TransactionMethod.RIDE);


        // finding driver amount after deduction of platform fee
        Double driverAmt = payment.getAmount() * (1 - PLATFORM_COMMISSION ); // 1 - 0.3 = 0.7

        //add money to driver wallet
        walletService.addMoneyToWallet(driver.getUser(), driverAmt, null,
                payment.getRide(), TransactionMethod.RIDE);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}
