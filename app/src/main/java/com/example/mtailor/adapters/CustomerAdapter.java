package com.example.mtailor.adapters;

import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtailor.activities.NewCustomerActivity;
import com.example.mtailor.R;
import com.example.mtailor.activities.NewOrderActivity;
import com.example.mtailor.activities.SelectProductActivity;
import com.example.mtailor.activities.ShowOrdersActivity;
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
        holder.rvCounterText.setText(String.valueOf(position+1));
        holder.rvCustomerName.setText(list.get(position).getCustomerName());
        holder.rvCustomerMobile.setText(list.get(position).getCustomerMobile());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (whatTODOhere){
                    case Util.SHOW_CUSTOMERS:
                        intent = new Intent(holder.linearLayout.getContext(), NewCustomerActivity.class);
                        intent.putExtra("origin", Util.UPDATE_CUSTOMER);
                        intent.putExtra("oldCustomer", list.get(position));
                        holder.linearLayout.getContext().startActivity(intent);
                        break;
                    case Util.TAKE_MEASUREMENTS:
                        intent = new Intent(holder.linearLayout.getContext(), SelectProductActivity.class);
                        intent.putExtra("origin", Util.CUSTOMER_MEASUREMENT);
                        intent.putExtra("oldCustomer",list.get(position));
                        holder.linearLayout.getContext().startActivity(intent);
                        break;
                    case Util.NEW_ORDER:
                        intent = new Intent(holder.linearLayout.getContext(), NewOrderActivity.class);
                        intent.putExtra("origin", Util.NEW_ORDER);
                        intent.putExtra("oldCustomer",list.get(position));
                        holder.linearLayout.getContext().startActivity(intent);
//                        Toast.makeText(holder.linearLayout.getContext(), "Wait i will take order", Toast.LENGTH_SHORT).show();
                        break;
                    case Util.SHOW_ORDERS:
                        intent = new Intent(holder.linearLayout.getContext(), ShowOrdersActivity.class);
                        intent.putExtra("origin", Util.SHOW_ORDERS);
                        intent.putExtra("oldCustomer",list.get(position));
                        holder.linearLayout.getContext().startActivity(intent);
//                        Toast.makeText(holder.linearLayout.getContext(), "Wait i will take order", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(holder.linearLayout.getContext(), "Something went wrong! Restart the App!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    //class for view holder
    public static class CustomerViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearLayout;
        TextView rvCounterText, rvCustomerName, rvCustomerMobile;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            rvCounterText = itemView.findViewById(R.id.counter_text);
            rvCustomerName = itemView.findViewById(R.id.rv_customer_name);
            rvCustomerMobile = itemView.findViewById(R.id.rv_customer_mobile);

        }
    }
}
