package com.adarsh45.mobitailor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adarsh45.mobitailor.R;
import com.adarsh45.mobitailor.pojo.Emp;

import java.util.ArrayList;

public class EmpAdapter extends RecyclerView.Adapter<EmpAdapter.EmpViewHolder> {

    private ArrayList<Emp> list;
    private EmployeeClick employeeClick;
    public EmpAdapter(ArrayList<Emp> empList, EmployeeClick employeeClick){
        this.list = empList;
        this.employeeClick = employeeClick;
    }

    @NonNull
    @Override
    public EmpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_emp_row,parent,false);
        return new EmpViewHolder(view, employeeClick);
    }

    @Override
    public void onBindViewHolder(@NonNull final EmpViewHolder holder, final int position) {
        holder.rvEmpNameText.setText(list.get(position).getEmpName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    //    class for viewHolder
    public static class EmpViewHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayout;
        TextView rvEmpNameText;
        ImageButton btnDeleteEmp;

        public EmpViewHolder(@NonNull View itemView, EmployeeClick employeeClick) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.rv_linear_layout_emp);
            rvEmpNameText = itemView.findViewById(R.id.rv_emp_name);
            btnDeleteEmp = itemView.findViewById(R.id.btn_delete_emp);

            itemView.setOnClickListener(v-> employeeClick.onEmployeeCardClick(getAdapterPosition()));
            btnDeleteEmp.setOnClickListener(v -> employeeClick.onEmpDeleteClick(getAdapterPosition()));
        }
    }

    public interface EmployeeClick{
        void onEmployeeCardClick(int position);
        void onEmpDeleteClick(int position);
    }
}
