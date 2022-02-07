package com.adarsh45.mobitailorapp.pojo;

import java.util.HashMap;

public class PaymentDetails {

    public class Payment{
        private String paymentDate, paymentAmount;
        public Payment(){}
        public Payment(String paymentDate, String paymentAmount){
            this.paymentDate = paymentDate;
            this.paymentAmount = paymentAmount;
        }
    }

    private String userUID;
    private String registrationDate;
    private HashMap<String, Payment> paymentsMap = new HashMap<>();
    private boolean isPaidMember = false;
    private long limit = 25;

    public PaymentDetails() {}

    public PaymentDetails(String userUID, String registrationDate, HashMap<String, Payment> paymentsMap, boolean isPaidMember, long limit) {
        this.userUID = userUID;
        this.registrationDate = registrationDate;
        this.paymentsMap = paymentsMap;
        this.isPaidMember = isPaidMember;
        this.limit = limit;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public HashMap<String, Payment> getPaymentsMap() {
        return paymentsMap;
    }

    public void setPaymentsMap(HashMap<String, Payment> paymentsMap) {
        this.paymentsMap = paymentsMap;
    }

    public boolean isPaidMember() {
        return isPaidMember;
    }

    public void setPaidMember(boolean paidMember) {
        isPaidMember = paidMember;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }
}
