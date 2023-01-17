package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferMoney {
    private BigDecimal transferMoney;
    private int senderId;
    private int receiverId;
    private BigDecimal senderBalance;
    private String tranferStatus;

    public String getTranferStatus() {
        return tranferStatus;
    }

    public void setTranferStatus(String tranferStatus) {
        this.tranferStatus = tranferStatus;
    }

    public BigDecimal getTransferMoney() {
        return transferMoney;
    }

    public void setTransferMoney(BigDecimal transferMoney) {
        this.transferMoney = transferMoney;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public BigDecimal getSenderBalance() {
        return senderBalance;
    }

    public void setSenderBalance(BigDecimal senderBalance) {
        this.senderBalance = senderBalance;
    }
}
