package com.dk.lb.model;

import java.util.Map;

public class Invoice {
    long rentalId;
    long invoiceNumber;
    long scanLine;
    long arSystemRef;
    long amount;
    Map<String, String> customFields;

    public long getRentalId() {
        return rentalId;
    }

    public void setRentalId(long rentalId) {
        this.rentalId = rentalId;
    }

    public long getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(long invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public long getScanLine() {
        return scanLine;
    }

    public void setScanLine(long scanLine) {
        this.scanLine = scanLine;
    }

    public long getArSystemRef() {
        return arSystemRef;
    }

    public void setArSystemRef(long arSystemRef) {
        this.arSystemRef = arSystemRef;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, String> customFields) {
        this.customFields = customFields;
    }
}
