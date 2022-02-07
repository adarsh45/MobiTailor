package com.adarsh45.mobitailorapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adarsh45.mobitailorapp.R;
import com.adarsh45.mobitailorapp.activities.OrderPdfActivity;
import com.adarsh45.mobitailorapp.pojo.Customer;
import com.adarsh45.mobitailorapp.pojo.Order;
import com.adarsh45.mobitailorapp.utils.EditOrderDialog;
import com.adarsh45.mobitailorapp.utils.LanguageHelper;

import java.util.ArrayList;

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
        Resources resources = LanguageHelper.updateLanguage(ctx);
        updateLanguage(view, resources);
//        Log.d(TAG, "onCreateViewHolder: "+ ((Activity)ctx).getClass().getSimpleName());
        return new OrderViewHolder(view);
    }

    private void updateLanguage(View view, Resources resources) {
        TextView tv;
        tv = view.findViewById(R.id.text_show_order_ref_no);
        tv.setText(resources.getString(R.string.order_ref_no));
        tv = view.findViewById(R.id.text_show_order_date);
        tv.setText(resources.getString(R.string.order_date));
        tv = view.findViewById(R.id.text_show_order_del_date);
        tv.setText(resources.getString(R.string.delivery_date));
        tv = view.findViewById(R.id.text_show_order_total);
        tv.setText(resources.getString(R.string.total));
        tv = view.findViewById(R.id.text_show_order_pending);
        tv.setText(resources.getString(R.string.pending));
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, int position) {
        final Order order = orders.get(position);
        holder.tvRvOrderRefNo.setText(order.getOrder_ref_no());
        holder.tvRvOrderDate.setText(order.getOrder_creation_date());
        holder.tvRvOrderDelDate.setText(order.getDelivery_date());
        holder.tvRvTotalBill.setText(order.getTotal_amount());
        holder.tvRvPendingBill.setText(order.getPending_amount());

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
            btnEditOrder.setOnClickListener(v -> onEditOrderClick());
            itemView.setOnClickListener(v -> openOrderDetails());
        }

//        onclick method for edit btn
        public void onEditOrderClick(){
            EditOrderDialog editOrderDialog = new EditOrderDialog(ctx, orders.get(getAdapterPosition()), customer.getCustomerID());
            editOrderDialog.show(fragmentManager, "Edit Order Dialog");
        }

//        onclick for whole itemview order
        public void openOrderDetails(){
            Intent intent = new Intent(ctx, OrderPdfActivity.class);
            intent.putExtra("customer", customer);
            intent.putExtra("order", orders.get(getAdapterPosition()));
            itemView.getContext().startActivity(intent);
        }
    }
}
