package com.blecua84.moneytransfers.router.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransferDTO {
    private AccountDTO from;
    private AccountDTO to;
    private String amount;
}
