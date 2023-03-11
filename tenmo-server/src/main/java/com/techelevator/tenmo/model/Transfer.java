package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private final String[] STATUS_OPTIONS = {"Pending", "Approved", "Rejected"};
    private final String[] TRANSFER_TYPES = {"Requested", "Sent"};
    private int transferId;
    private int transferStatus;
    private int transferType;

    private int accountFrom;
    private int accountTo;
    private BigDecimal amount;

    public Transfer(int transferStatus, int transferType, int accountFrom, int accountTo, BigDecimal amount) {
        this.transferStatus = transferStatus;
        this.transferType = transferType;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    public Transfer() {
    }
    public String getTransferTypeString(int index){
        return TRANSFER_TYPES[index-1];

    }
    public String getTransferStatusAsString(int index){
        return STATUS_OPTIONS[index-1];

    }
    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferStatus(){
        return this.transferStatus;
    }
    public int getTransferType() {
        return transferType;
    }

    public void setTransferStatus(int transferStatus) {
        this.transferStatus = transferStatus;
    }



    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "transferId=" + transferId +
                ", transferStatus='" + transferStatus + '\'' +
                ", transferType='" + transferType + '\'' +
                ", accountFrom=" + accountFrom +
                ", accountTo=" + accountTo +
                ", amount=" + amount +
                '}';
    }
    public String toLabelString (){
        return " Id: " + transferId + "   Status: " + getTransferStatusAsString(transferStatus) +
                "   Type: " + getTransferTypeString(transferType) +
                "   Amount: $" + amount;

    }
    public String toRequestLabelString (){
        return " Id: " + transferId + "   Status: " + getTransferStatusAsString(transferStatus) +
                "   Amount: $" + amount;

    }
}