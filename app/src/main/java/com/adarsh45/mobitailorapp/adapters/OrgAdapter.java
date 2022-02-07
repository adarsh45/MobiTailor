package com.adarsh45.mobitailorapp.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adarsh45.mobitailorapp.R;
import com.adarsh45.mobitailorapp.activities.NewEmployeeActivity;
import com.adarsh45.mobitailorapp.activities.NewOrganizationActivity;
import com.adarsh45.mobitailorapp.pojo.Org;
import com.adarsh45.mobitailorapp.utils.Util;

import java.util.ArrayList;

public class OrgAdapter extends RecyclerView.Adapter<OrgAdapter.OrgViewHolder> {

    private static final String TAG = "OrgAdapter";
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
        holder.rvOrgCount.setText(String.valueOf(position+1));
        holder.rvOrgName.setText(list.get(position).getOrgName());
        holder.rvOrgOwner.setText(list.get(position).getOrgOwner());

        holder.linearLayout.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: ORIGIN: "+ whatTODOhere);
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
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    //class for view holder
    public static class OrgViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearLayout;
        TextView rvOrgCount, rvOrgName, rvOrgOwner;

        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            rvOrgCount = itemView.findViewById(R.id.counter_text);
            rvOrgName = itemView.findViewById(R.id.rv_customer_name);
            rvOrgOwner = itemView.findViewById(R.id.rv_customer_mobile);
        }

    }
}
