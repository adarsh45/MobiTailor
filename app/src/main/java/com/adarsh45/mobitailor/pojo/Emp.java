package com.adarsh45.mobitailor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class Emp implements Parcelable {
    private String empID;
    private String empName;

    public Emp(){}

    public Emp(String empID, String empName){
        this.empID = empID;
        this.empName = empName;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.empID);
        dest.writeString(this.empName);
    }

    protected Emp(Parcel in) {
        this.empID = in.readString();
        this.empName = in.readString();
    }

    public static final Parcelable.Creator<Emp> CREATOR = new Parcelable.Creator<Emp>() {
        @Override
        public Emp createFromParcel(Parcel source) {
            return new Emp(source);
        }

        @Override
        public Emp[] newArray(int size) {
            return new Emp[size];
        }
    };
}
