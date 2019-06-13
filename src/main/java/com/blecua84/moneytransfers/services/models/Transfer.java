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
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "fk_from")
    private Account from;

    @OneToOne
    @JoinColumn(name = "fk_to")
    private Account to;

    @Column(name = "amount")
    private BigDecimal amount;

    public Transfer(Account from, Account to, BigDecimal amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}
