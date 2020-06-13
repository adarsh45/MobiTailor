package com.example.mtailor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Org implements Parcelable {

    private String orgID, orgName, orgOwner, orgMobile, orgAddress, orgClothColor, orgEmbroidery, orgNotes;

    public Org() {}

    public Org(String orgID, String orgName, String orgOwner, String orgMobile, String orgAddress, String orgClothColor, String orgEmbroidery, String orgNotes) {
        this.orgID = orgID;
        this.orgName = orgName;
        this.orgOwner = orgOwner;
        this.orgMobile = orgMobile;
        this.orgAddress = orgAddress;
        this.orgClothColor = orgClothColor;
        this.orgEmbroidery = orgEmbroidery;
        this.orgNotes = orgNotes;
    }

    public String getOrgID() {
        return orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(String orgOwner) {
        this.orgOwner = orgOwner;
    }

    public String getOrgMobile() {
        return orgMobile;
    }

    public void setOrgMobile(String orgMobile) {
        this.orgMobile = orgMobile;
    }

    public String getOrgAddress() {
        return orgAddress;
    }

    public void setOrgAddress(String orgAddress) {
        this.orgAddress = orgAddress;
    }

    public String getOrgClothColor() {
        return orgClothColor;
    }

    public void setOrgClothColor(String orgClothColor) {
        this.orgClothColor = orgClothColor;
    }

    public String getOrgEmbroidery() {
        return orgEmbroidery;
    }

    public void setOrgEmbroidery(String orgEmbroidery) {
        this.orgEmbroidery = orgEmbroidery;
    }

    public String getOrgNotes() {
        return orgNotes;
    }

    public void setOrgNotes(String orgNotes) {
        this.orgNotes = orgNotes;
    }

//    parcelable code


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
        dest.writeString(this.orgClothColor);
        dest.writeString(this.orgEmbroidery);
        dest.writeString(this.orgNotes);
    }

    protected Org(Parcel in) {
        this.orgID = in.readString();
        this.orgName = in.readString();
        this.orgOwner = in.readString();
        this.orgMobile = in.readString();
        this.orgAddress = in.readString();
        this.orgClothColor = in.readString();
        this.orgEmbroidery = in.readString();
        this.orgNotes = in.readString();
    }

    public static final Creator<Org> CREATOR = new Creator<Org>() {
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
