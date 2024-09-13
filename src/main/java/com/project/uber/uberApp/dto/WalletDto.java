package com.project.uber.uberApp.dto;

import lombok.Data;

import java.util.List;


@Data
public class WalletDto {

    private Long id;

    private UserDto user;   // Each User have one to Mapping with Wallet

    private Double balance;

    private List<WalletTransactionDto> transactions;
}
