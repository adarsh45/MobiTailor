package com.example.mtailor.pojo;

public class Profile {

    public String userUID;
    public String shopName;
    public String ownerName;
    public String shopAddress;
    public String ownerMobile;

    public Profile(){

    }

    public Profile(String userUID, String shopName, String ownerName, String shopAddress, String ownerMobile) {
        this.userUID = userUID;
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.shopAddress = shopAddress;
        this.ownerMobile = ownerMobile;
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
