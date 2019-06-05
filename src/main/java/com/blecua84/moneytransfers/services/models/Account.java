package com.blecua84.moneytransfers.services.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "sortCode")
    private String sortCode;

    @Column(name = "accountNumber")
    private String accountNumber;

    @Column(name = "available")
    private float available;

    public Account(String sortCode, String accountNumber, float available) {
        this.sortCode = sortCode;
        this.accountNumber = accountNumber;
        this.available = available;
    }
}
