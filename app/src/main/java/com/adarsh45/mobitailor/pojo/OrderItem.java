package com.adarsh45.mobitailor.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderItem implements Parcelable {

    private String item_name, item_quantity, item_rate, total;

    public OrderItem(){}

    public OrderItem(String item_name, String item_quantity, String item_rate, String total) {
        this.item_name = item_name;
        this.item_quantity = item_quantity;
        this.item_rate = item_rate;
        this.total = total;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getItem_quantity() {
        return item_quantity;
    }

    public void setItem_quantity(String item_quantity) {
        this.item_quantity = item_quantity;
    }

    public String getItem_rate() {
        return item_rate;
    }

    public void setItem_rate(String item_rate) {
        this.item_rate = item_rate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.item_name);
        dest.writeString(this.item_quantity);
        dest.writeString(this.item_rate);
        dest.writeString(this.total);
    }

    protected OrderItem(Parcel in) {
        this.item_name = in.readString();
        this.item_quantity = in.readString();
        this.item_rate = in.readString();
        this.total = in.readString();
    }

    public static final Parcelable.Creator<OrderItem> CREATOR = new Parcelable.Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel source) {
            return new OrderItem(source);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };
}
