package com.example.mtailor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Org {

    private String orgID, orgName, orgOwner, orgMobile, orgAddress;

    public Org() {}

    public Org(String orgID,String orgName, String orgOwner,String orgMobile,String orgAddress) {
        this.orgID = orgID;
        this.orgName = orgName;
        this.orgOwner = orgOwner;
        this.orgMobile = orgMobile;
        this.orgAddress = orgAddress;
    }

    public String getOrgID() {
        return this.orgID;
    }

    public void setOrgID(String value) {
        this.orgID = value;
    }

    public String getOrgName() {
        return this.orgName;
    }

    public void setOrgName(String value) {
        this.orgName = value;
    }

    public String getOrgOwner() {
        return this.orgOwner;
    }

    public void setOrgOwner(String value) {
        this.orgOwner = value;
    }

    public String getOrgMobile() {
        return this.orgMobile;
    }

    public void setOrgMobile(String value) {
        this.orgMobile = value;
    }

    public String getOrgAddress() {
        return this.orgAddress;
    }

    public void setOrgAddress(String value) {
        this.orgAddress = value;
    }


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Customer> CREATOR = new Parcelable.Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

}
