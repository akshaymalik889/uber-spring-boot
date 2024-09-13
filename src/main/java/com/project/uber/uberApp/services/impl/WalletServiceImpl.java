package com.project.uber.uberApp.services.impl;

import com.project.uber.uberApp.entities.Ride;
import com.project.uber.uberApp.entities.User;
import com.project.uber.uberApp.entities.Wallet;
import com.project.uber.uberApp.entities.WalletTransaction;
import com.project.uber.uberApp.entities.enums.TransactionMethod;
import com.project.uber.uberApp.entities.enums.TransactionType;
import com.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.project.uber.uberApp.repositories.WalletRepository;
import com.project.uber.uberApp.services.WalletService;
import com.project.uber.uberApp.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final ModelMapper modelMapper;
    private final WalletTransactionService walletTransactionService;


    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {

        //get Wallet of the User
        Wallet wallet = findByUser(user);

        //add new amount to previous wallet amount
        wallet.setBalance(wallet.getBalance() + amount);

        //make object of  Wallet Transaction using Builder Pattern
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.CREDIT)
                .transactionMethod(transactionMethod)
                .amount(amount)
                .build();

        //save in wallet Transaction Repository
        walletTransactionService.createNewWalletTransaction(walletTransaction);

        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet deductMoneyFromWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {

        //get Wallet of the User
        Wallet wallet = findByUser(user);

        //deduct  amount from previous wallet amount
        wallet.setBalance(wallet.getBalance() - amount);

        //make object of  Wallet Transaction using Builder Pattern
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .transactionType(TransactionType.DEBIT)
                .transactionMethod(transactionMethod)
                .amount(amount)
                .build();

        //save in wallet Transaction Repository
        walletTransactionService.createNewWalletTransaction(walletTransaction);
        //wallet.getTransactions().add(walletTransaction);

        return walletRepository.save(wallet);
    }

    //this function used to transfer all money from wallet to bank account
    @Override
    public void withdrawAllMyMoneyFromWallet() {

    }

    @Override
    public Wallet findWalletById(Long walletId) {

        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet Not Found With Id: "+walletId));
    }

    @Override
    public Wallet createNewWallet(User user) {

        //create Wallet Object
        Wallet wallet = new Wallet();
        //set user
        wallet.setUser(user);
        //save in repo
        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findByUser(User user) {

        return  walletRepository.findByUser(user)
                .orElseThrow( () -> new ResourceNotFoundException("Wallet Not Found for User With Id: "+user.getId()));

    }
}
