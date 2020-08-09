package com.example.mtailor.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Profile {

    private String userUID, shopName, ownerName, shopAddress, ownerMobile;
    private long amountPaid;

    public Profile(){}

    public Profile(String userUID, String shopName, String ownerName, String shopAddress, String ownerMobile, long amountPaid) {
        this.userUID = userUID;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.shopAddress = shopAddress;
        this.ownerMobile = ownerMobile;
        this.amountPaid = amountPaid;
    }

    public Profile(String userUID, String shopName, String ownerName, String shopAddress, String ownerMobile) {
        this.userUID = userUID;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.shopAddress = shopAddress;
        this.ownerMobile = ownerMobile;
    }

    public long getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(int amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getOwnerMobile() {
        return ownerMobile;
    }

    public void setOwnerMobile(String ownerMobile) {
        this.ownerMobile = ownerMobile;
    }
}
