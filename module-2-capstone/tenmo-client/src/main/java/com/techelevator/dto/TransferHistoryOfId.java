package com.techelevator.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransferHistoryOfId {
    private int transferId;
    private String accountFrom;
    private String accountTo;
    private String transferStatusId;
    private String transferTypeId;
    private BigDecimal amount;

}
