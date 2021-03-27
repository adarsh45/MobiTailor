package com.adarsh45.mobitailor.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adarsh45.mobitailor.R;
import com.adarsh45.mobitailor.activities.NewEmployeeActivity;
import com.adarsh45.mobitailor.activities.NewOrganizationActivity;
import com.adarsh45.mobitailor.pojo.Org;
import com.adarsh45.mobitailor.utils.Util;

import java.util.ArrayList;

public class OrgAdapter extends RecyclerView.Adapter<OrgAdapter.OrgViewHolder> {

    private ArrayList<Org> list;
    private byte whatTODOhere;

    public OrgAdapter(ArrayList<Org> list, byte whatTODO){
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
                if (whatTODOhere == Util.UPDATE_ORGANIZATION){

                    Intent intent = new Intent(holder.linearLayout.getContext(), NewOrganizationActivity.class);
                    intent.putExtra("origin", Util.UPDATE_ORGANIZATION);
                    intent.putExtra("oldOrg", list.get(position)); // parcel whole org object to intent
                    holder.linearLayout.getContext().startActivity(intent);

                } else if (whatTODOhere == Util.NEW_EMPLOYEE){

                    Intent intent = new Intent(holder.linearLayout.getContext(), NewEmployeeActivity.class);
                    intent.putExtra("origin", Util.NEW_EMPLOYEE);
                    intent.putExtra("oldOrg", list.get(position)); // parcel whole org object to intent
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
    public static class OrgViewHolder extends RecyclerView.ViewHolder{

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
