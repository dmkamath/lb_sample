package com.dk.lb.model;

public class Kiosk {
    // kioskId represents the group of payments that
    // a kiosk collects.
    long kioskId;
    long hotelId;
    String depositDate;
    long kioskAmount;
    long paymentCount;
    long invoiceCount;
    Rental rental;

    public Rental getRental() {
        return rental;
    }

    public void setRental(Rental rental) {
        this.rental = rental;
    }

    public long getKioskId() {
        return kioskId;
    }

    public void setKioskId(long kioskId) {
        this.kioskId = kioskId;
    }

    public long getHotelId() {
        return hotelId;
    }

    public void setHotelId(long hotelId) {
        this.hotelId = hotelId;
    }

    public String getDepositDate() {
        return depositDate;
    }

    public void setDepositDate(String depositDate) {
        this.depositDate = depositDate;
    }

    public long getKioskAmount() {
        return kioskAmount;
    }

    public void setKioskAmount(long kioskAmount) {
        this.kioskAmount = kioskAmount;
    }

    public long getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(long paymentCount) {
        this.paymentCount = paymentCount;
    }

    public long getInvoiceCount() {
        return invoiceCount;
    }

    public void setInvoiceCount(long invoiceCount) {
        this.invoiceCount = invoiceCount;
    }
}
