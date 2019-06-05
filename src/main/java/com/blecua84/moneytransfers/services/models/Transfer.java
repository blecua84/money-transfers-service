package com.blecua84.moneytransfers.services.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne
    @JoinColumn(name = "fk_from")
    private Account from;

    @OneToOne
    @JoinColumn(name = "fk_to")
    private Account to;

    @Column(name = "amount")
    private float amount;

    public Transfer(Account from, Account to, float amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}
