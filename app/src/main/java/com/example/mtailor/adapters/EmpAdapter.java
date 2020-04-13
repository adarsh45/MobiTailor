package com.example.mtailor.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtailor.R;
import com.example.mtailor.activities.SelectProductActivity;
import com.example.mtailor.pojo.Emp;

import java.util.ArrayList;

public class EmpAdapter extends RecyclerView.Adapter<EmpAdapter.EmpViewHolder> {

    private ArrayList<Emp> list;

    public EmpAdapter(ArrayList<Emp> empList){
        this.list = empList;
    }

    @NonNull
    @Override
    public EmpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_emp_row,parent,false);
        return new EmpAdapter.EmpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EmpViewHolder holder, final int position) {
        holder.rvEmpNameText.setText(list.get(position).getEmpName());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.linearLayout.getContext(), SelectProductActivity.class);
                intent.putExtra("origin","empMeasurement");
                intent.putExtra("emp",list.get(position));
                holder.linearLayout.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    //    class for viewHolder
    public class EmpViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView rvEmpNameText;

    public EmpViewHolder(@NonNull View itemView) {
        super(itemView);
        linearLayout = itemView.findViewById(R.id.rv_linear_layout_emp);
        rvEmpNameText = itemView.findViewById(R.id.rv_emp_name);
    }
}
}
