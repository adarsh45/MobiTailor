package com.example.mtailor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtailor.R;
import com.example.mtailor.activities.NewOrderActivity;
import com.example.mtailor.activities.OrderPdfActivity;
import com.example.mtailor.pojo.Customer;
import com.example.mtailor.pojo.Order;
import com.example.mtailor.utils.Util;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private Context ctx;
    private Customer customer;
    private ArrayList<Order> orders;

    public OrdersAdapter(Context context,Customer customer ,ArrayList<Order> orderArrayList){
        this.ctx = context;
        this.customer = customer;
        this.orders = orderArrayList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, int position) {
        final Order order = orders.get(position);
        holder.tvRvOrderRefNo.setText("Ref.No: "+order.getOrder_ref_no());
        holder.tvRvOrderDate.setText(order.getOrder_creation_date());
        holder.tvRvOrderDelDate.setText(order.getDelivery_date());
        holder.tvRvTotalBill.setText("Total Bill: ₹ "+ order.getTotal_amount());
        holder.tvRvPendingBill.setText("Pending: ₹ "+ order.getPending_amount());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, OrderPdfActivity.class);
                intent.putExtra("customer", customer);
                intent.putExtra("order", order);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{

        public TextView tvRvOrderRefNo,  tvRvOrderDate, tvRvOrderDelDate, tvRvTotalBill, tvRvPendingBill;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRvOrderRefNo = itemView.findViewById(R.id.tv_rv_order_ref_no);
            tvRvOrderDate = itemView.findViewById(R.id.tv_rv_order_date);
            tvRvOrderDelDate = itemView.findViewById(R.id.tv_rv_order_del_date);
            tvRvTotalBill = itemView.findViewById(R.id.tv_rv_order_total_bill);
            tvRvPendingBill = itemView.findViewById(R.id.tv_rv_order_pending_bill);
        }
    }
}
