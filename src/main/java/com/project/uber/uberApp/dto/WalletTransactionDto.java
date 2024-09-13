package com.project.uber.uberApp.dto;

import com.project.uber.uberApp.entities.enums.TransactionMethod;
import com.project.uber.uberApp.entities.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionDto  {

    private Long id;

    private Double amount;

    private TransactionType transactionType;   // Can be Debit or Credit Transaction

    private TransactionMethod transactionMethod; // can be Banking or Ride Type

    private RideDto ride;  //one to one mapping with ride because one ride belong to one transaction

    private String transactionId;

    private WalletDto wallet;  // one wallet have many transactions

    private LocalDateTime timeStamp;
}
