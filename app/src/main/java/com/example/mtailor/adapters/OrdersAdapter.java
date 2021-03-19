package com.example.mtailor.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtailor.R;
import com.example.mtailor.activities.NewCustomerActivity;
import com.example.mtailor.activities.NewOrderActivity;
import com.example.mtailor.activities.OrderPdfActivity;
import com.example.mtailor.pojo.Customer;
import com.example.mtailor.pojo.Order;
import com.example.mtailor.utils.EditOrderDialog;
import com.example.mtailor.utils.ResultDialog;
import com.example.mtailor.utils.Util;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private static final String TAG = "OrdersAdapter";

    private Context ctx;
    private FragmentManager fragmentManager;
    private Customer customer;
    private ArrayList<Order> orders;

    public OrdersAdapter( Context context,FragmentManager fragmentManager, Customer customer, ArrayList<Order> orderArrayList){
        this.ctx = context;
        this.fragmentManager = fragmentManager;
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

//        holder.btnEditOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ctx, OrderPdfActivity.class);
//                intent.putExtra("customer", customer);
//                intent.putExtra("order", order);
//                holder.itemView.getContext().startActivity(intent);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder{

        public TextView tvRvOrderRefNo,  tvRvOrderDate, tvRvOrderDelDate, tvRvTotalBill, tvRvPendingBill;
        public ImageView btnEditOrder;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRvOrderRefNo = itemView.findViewById(R.id.tv_rv_order_ref_no);
            tvRvOrderDate = itemView.findViewById(R.id.tv_rv_order_date);
            tvRvOrderDelDate = itemView.findViewById(R.id.tv_rv_order_del_date);
            tvRvTotalBill = itemView.findViewById(R.id.tv_rv_order_total_bill);
            tvRvPendingBill = itemView.findViewById(R.id.tv_rv_order_pending_bill);
            btnEditOrder = itemView.findViewById(R.id.btn_edit_order);
            btnEditOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditOrderClick();
                }
            });
        }

        public void onEditOrderClick(){
            EditOrderDialog editOrderDialog = new EditOrderDialog(ctx, orders.get(getAdapterPosition()), customer.getCustomerID());
            editOrderDialog.show(fragmentManager, "Edit Order Dialog");
        }


//        @Override
//        public void onClick(View v) {
//            if (v.getId() == R.id.btn_edit_order){
//                EditOrderDialog editOrderDialog = new EditOrderDialog(ctx.getApplicationContext(), orders.get(getAdapterPosition()), customer.getCustomerID());
////                editOrderDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////                editOrderDialog.setContentView(R.layout.dialog_edit_order);
//                editOrderDialog.show();
//            } else {
//                Intent intent = new Intent(ctx, OrderPdfActivity.class);
//                intent.putExtra("customer", customer);
//                intent.putExtra("order", orders.get(getAdapterPosition()));
//                itemView.getContext().startActivity(intent);
//            }
//            Log.d(TAG, "onClick: " + v.getId());
//        }
    }



}
