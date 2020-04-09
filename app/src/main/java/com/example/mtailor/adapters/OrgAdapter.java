package com.example.mtailor.adapters;

import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mtailor.R;
import com.example.mtailor.activities.NewCustomerActivity;
import com.example.mtailor.activities.NewEmployeeActivity;
import com.example.mtailor.activities.NewOrganizationActivity;
import com.example.mtailor.activities.SelectProductActivity;
import com.example.mtailor.activities.ShowCustomersActivity;
import com.example.mtailor.pojo.Org;

import java.util.ArrayList;

public class OrgAdapter extends RecyclerView.Adapter<OrgAdapter.OrgViewHolder> {

    private ArrayList<Org> list;
    public static final int SHOW_ORG = 3;
    public static final int ADD_NEW_EMPLOYEE = 4;
    private int whatTODOhere;

    public OrgAdapter(ArrayList<Org> list, int whatTODO){
        this.list = list;
        this.whatTODOhere = whatTODO;
    }

    @NonNull
    @Override
    public OrgAdapter.OrgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_row, parent, false);

        return new OrgAdapter.OrgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrgAdapter.OrgViewHolder holder, final int position) {
        holder.rvOrgName.setText(list.get(position).getOrgName());
        holder.rvOrgOwner.setText(list.get(position).getOrgOwner());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whatTODOhere == SHOW_ORG){

                    Intent intent = new Intent(holder.linearLayout.getContext(), NewOrganizationActivity.class);
                    intent.putExtra("origin", "updateOrg");
                    intent.putExtra("oldOrg", list.get(position)); // parcel whole org object to intent
                    holder.linearLayout.getContext().startActivity(intent);

                } else if (whatTODOhere == ADD_NEW_EMPLOYEE){

                    Intent intent = new Intent(holder.linearLayout.getContext(), NewEmployeeActivity.class);
                    intent.putExtra("origin", "employee");
                    intent.putExtra("oldOrg", list.get(position)); // parcel whole org object to intent
                    holder.linearLayout.getContext().startActivity(intent);

                    Log.d("TAG","ADD NEW EMPLOYEE HERE");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    //class for view holder
    public class OrgViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearLayout;
        TextView rvOrgName, rvOrgOwner;

        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            rvOrgName = itemView.findViewById(R.id.rv_customer_name);
            rvOrgOwner = itemView.findViewById(R.id.rv_customer_mobile);

        }
    }
}
