package com.example.mtailor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Org implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orgID);
        dest.writeString(this.orgName);
        dest.writeString(this.orgOwner);
        dest.writeString(this.orgMobile);
        dest.writeString(this.orgAddress);
    }

    protected Org(Parcel in) {
        this.orgID = in.readString();
        this.orgName = in.readString();
        this.orgOwner = in.readString();
        this.orgMobile = in.readString();
        this.orgAddress = in.readString();
    }

    public static final Parcelable.Creator<Org> CREATOR = new Parcelable.Creator<Org>() {
        @Override
        public Org createFromParcel(Parcel source) {
            return new Org(source);
        }

        @Override
        public Org[] newArray(int size) {
            return new Org[size];
        }
    };
}
