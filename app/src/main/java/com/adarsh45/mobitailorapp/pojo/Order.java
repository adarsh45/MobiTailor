package com.adarsh45.mobitailorapp.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Order implements Parcelable {

    private String order_id, order_ref_no, order_creation_date, delivery_date, total_amount, advance_amount, pending_amount;

    private OrderItem item_1, item_2, item_3, item_4;

    public Order(){}

    public Order(String order_creation_date, String delivery_date,
                 String total_amount, String advance_amount, String pending_amount,
                 OrderItem item_1, OrderItem item_2, OrderItem item_3, OrderItem item_4) {
        this.order_creation_date = order_creation_date;
        this.delivery_date = delivery_date;
        this.total_amount = total_amount;
        this.advance_amount = advance_amount;
        this.pending_amount = pending_amount;
        this.item_1 = item_1;
        this.item_2 = item_2;
        this.item_3 = item_3;
        this.item_4 = item_4;
    }

    public Order(String order_id, String order_ref_no, String order_creation_date, String delivery_date, String total_amount, String advance_amount, String pending_amount, OrderItem item_1, OrderItem item_2, OrderItem item_3, OrderItem item_4) {
        this.order_id = order_id;
        this.order_ref_no = order_ref_no;
        this.order_creation_date = order_creation_date;
        this.delivery_date = delivery_date;
        this.total_amount = total_amount;
        this.advance_amount = advance_amount;
        this.pending_amount = pending_amount;
        this.item_1 = item_1;
        this.item_2 = item_2;
        this.item_3 = item_3;
        this.item_4 = item_4;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_ref_no() {
        return order_ref_no;
    }

    public void setOrder_ref_no(String order_ref_no) {
        this.order_ref_no = order_ref_no;
    }

    public String getOrder_creation_date() {
        return order_creation_date;
    }

    public void setOrder_creation_date(String order_creation_date) {
        this.order_creation_date = order_creation_date;
    }

    public String getDelivery_date() {
        return delivery_date;
    }

    public void setDelivery_date(String delivery_date) {
        this.delivery_date = delivery_date;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getAdvance_amount() {
        return advance_amount;
    }

    public void setAdvance_amount(String advance_amount) {
        this.advance_amount = advance_amount;
    }

    public String getPending_amount() {
        return pending_amount;
    }

    public void setPending_amount(String pending_amount) {
        this.pending_amount = pending_amount;
    }

    public OrderItem getItem_1() {
        return item_1;
    }

    public void setItem_1(OrderItem item_1) {
        this.item_1 = item_1;
    }

    public OrderItem getItem_2() {
        return item_2;
    }

    public void setItem_2(OrderItem item_2) {
        this.item_2 = item_2;
    }

    public OrderItem getItem_3() {
        return item_3;
    }

    public void setItem_3(OrderItem item_3) {
        this.item_3 = item_3;
    }

    public OrderItem getItem_4() {
        return item_4;
    }

    public void setItem_4(OrderItem item_4) {
        this.item_4 = item_4;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.order_id);
        dest.writeString(this.order_ref_no);
        dest.writeString(this.order_creation_date);
        dest.writeString(this.delivery_date);
        dest.writeString(this.total_amount);
        dest.writeString(this.advance_amount);
        dest.writeString(this.pending_amount);
        dest.writeParcelable(this.item_1, flags);
        dest.writeParcelable(this.item_2, flags);
        dest.writeParcelable(this.item_3, flags);
        dest.writeParcelable(this.item_4, flags);
    }

    protected Order(Parcel in) {
        this.order_id = in.readString();
        this.order_ref_no = in.readString();
        this.order_creation_date = in.readString();
        this.delivery_date = in.readString();
        this.total_amount = in.readString();
        this.advance_amount = in.readString();
        this.pending_amount = in.readString();
        this.item_1 = in.readParcelable(OrderItem.class.getClassLoader());
        this.item_2 = in.readParcelable(OrderItem.class.getClassLoader());
        this.item_3 = in.readParcelable(OrderItem.class.getClassLoader());
        this.item_4 = in.readParcelable(OrderItem.class.getClassLoader());
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
