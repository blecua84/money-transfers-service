package com.blecua84.moneytransfers.services.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {
    private Account from;
    private Account to;
    private float amount;
}
