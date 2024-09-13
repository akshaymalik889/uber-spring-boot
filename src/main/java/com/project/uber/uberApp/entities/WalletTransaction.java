package com.project.uber.uberApp.entities;

import com.project.uber.uberApp.entities.enums.TransactionMethod;
import com.project.uber.uberApp.entities.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(indexes = {
        @Index(name = "idx_wallet_transaction_wallet", columnList = "wallet_id"),
        @Index(name = "idx_wallet_transaction_ride", columnList = "ride_id")
})

public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private TransactionType transactionType;   // Can be Debit or Credit Transaction

    private TransactionMethod transactionMethod; // can be Banking or Ride Type

    @ManyToOne
    private Ride ride;  // because one ride belong to two transactions , 1. for driver, 2nd for Rider

    private String transactionId;

    @ManyToOne
    private Wallet wallet;  // one wallet have many transactions

    @CreationTimestamp
    private LocalDateTime timeStamp;

}
