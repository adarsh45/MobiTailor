package com.example.mtailor.adapters;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtailor.activities.NewCustomerActivity;
import com.example.mtailor.R;
import com.example.mtailor.activities.SelectProductActivity;
import com.example.mtailor.pojo.Customer;
import com.example.mtailor.utils.Util;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private ArrayList<Customer> list;
    private byte whatTODOhere;

    public CustomerAdapter(ArrayList<Customer> list, byte whatTODO){
        this.list = list;
        whatTODOhere = whatTODO;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_row, parent, false);

        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerViewHolder holder, final int position) {
        holder.rvCustomerName.setText(list.get(position).getCustomerName());
        holder.rvCustomerMobile.setText(list.get(position).getCustomerMobile());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whatTODOhere == Util.SHOW_CUSTOMERS){
                    Intent intent = new Intent(holder.linearLayout.getContext(), NewCustomerActivity.class);
                    intent.putExtra("origin", Util.UPDATE_CUSTOMER);
                    intent.putExtra("oldCustomer", list.get(position));
                    holder.linearLayout.getContext().startActivity(intent);
                } else if (whatTODOhere == Util.TAKE_MEASUREMENTS){
                    Intent intent = new Intent(holder.linearLayout.getContext(), SelectProductActivity.class);
                    intent.putExtra("origin", Util.CUSTOMER_MEASUREMENT);
                    intent.putExtra("oldCustomer",list.get(position));
                    holder.linearLayout.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    //class for view holder
    public class CustomerViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearLayout;
        TextView rvCustomerName, rvCustomerMobile;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            rvCustomerName = itemView.findViewById(R.id.rv_customer_name);
            rvCustomerMobile = itemView.findViewById(R.id.rv_customer_mobile);

        }
    }
}
