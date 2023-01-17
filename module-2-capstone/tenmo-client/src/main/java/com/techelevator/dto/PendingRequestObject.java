package com.techelevator.dto;

public class PendingRequestObject {
    private long transferId;
    private long accountTo;

    public long getTransferId() {
        return transferId;
    }

    public long getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(long accountTo) {
        this.accountTo = accountTo;
    }

    public void setTransferId(long transferId) {
        this.transferId = transferId;
    }
}
