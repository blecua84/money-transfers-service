package com.blecua84.moneytransfers.services.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private int id;

    @Column(name = "sortCode")
    private String sortCode;

    @Column(name = "accountNumber")
    private String accountNumber;

    @Column(name = "available")
    private BigDecimal available;

    public Account(String sortCode, String accountNumber, BigDecimal available) {
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.available = available;
    }
}
