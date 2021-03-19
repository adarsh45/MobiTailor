package com.example.mtailor.utils;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditOrderDialog extends DialogFragment {
    
    private static final String TAG = "EditOrderDialog";

    private Context context;
    private Order order;
    private String customerId;
    final Calendar myCalendar = Calendar.getInstance();

//    views
    private EditText etDatePicker, etAdvanceAmount;
    private TextView tvOrderRefNo, tvFinalTotal, tvPendingAmount;

//    firebase
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference ordersRef = FirebaseDatabase.getInstance()
            .getReference("Users")
            .child(currentUser.getUid())
            .child("Orders");

    public EditOrderDialog(@NonNull Context context, Order order, String customerId){
        this.context = context;
        this.order = order;
        this.customerId = customerId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_order,container,false);

        this.setCancelable(false);

        tvOrderRefNo = view.findViewById(R.id.tv_edit_order_ref_no);
        etDatePicker = view.findViewById(R.id.edit_order_date_picker);
        etAdvanceAmount = view.findViewById(R.id.et_edit_advance);
        tvFinalTotal = view.findViewById(R.id.tv_edit_final_total);
        tvPendingAmount = view.findViewById(R.id.tv_edit_pending);
        Button btnSaveEditOrder = view.findViewById(R.id.btn_save_edit_order);
        Button btnCancelEditOrder = view.findViewById(R.id.btn_cancel_edit_order);

        setTexts();

        advanceAmtChangeListener();

        pickDateForOrder();

        btnSaveEditOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrder();
            }
        });

        btnCancelEditOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void setTexts() {
        if (order == null || customerId == null){
            Log.d(TAG, "setTexts: Order or CustomerID is null");
            Toast.makeText(context, "Order not found! Please try again!", Toast.LENGTH_SHORT).show();
            return;
        }
        tvOrderRefNo.setText(order.getOrder_ref_no());
        etDatePicker.setText(order.getDelivery_date());
        tvFinalTotal.setText(order.getTotal_amount());
        etAdvanceAmount.setText(order.getAdvance_amount());
        tvPendingAmount.setText(order.getPending_amount());
    }

    private void editOrder() {
        String newDelDate = etDatePicker.getText().toString();
        String newAdvanceAmount = etAdvanceAmount.getText().toString();
        String newPendingAmount = tvPendingAmount.getText().toString();

        order.setDelivery_date(newDelDate);
        order.setAdvance_amount(newAdvanceAmount);
        order.setPending_amount(newPendingAmount);

        ordersRef = ordersRef
                .child(customerId)
                .child(order.getOrder_id());
        
        ordersRef.setValue(order)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dismiss();
                            Log.d(TAG, "onComplete: Order updated successfully!");
                            Toast.makeText(context, "Successfully updated order", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d(TAG, "onComplete: order failed to update");
                            Toast.makeText(context, "Failed to update the Order!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void pickDateForOrder() {
        etDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };

                new DatePickerDialog(
                        getActivity(),
                        datePickerListener,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        etDatePicker.setText(sdf.format(myCalendar.getTime()));
    }

    private void advanceAmtChangeListener(){
        etAdvanceAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //    values for calculation
                int  intTotal = 0, intAdvance = 0, intPending = 0;
                intAdvance = TextUtils.isEmpty(s) ? 0 : Integer.parseInt(s.toString());
                intTotal = Integer.parseInt(tvFinalTotal.getText().toString());
                intPending = Integer.parseInt(tvPendingAmount.getText().toString());

                if (intAdvance > intTotal){
                    etAdvanceAmount.setError("Advance cannot be greater than Total Bill!");
                    return;
                }
                intPending = intTotal - intAdvance;
                tvPendingAmount.setText(String.valueOf(intPending));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
