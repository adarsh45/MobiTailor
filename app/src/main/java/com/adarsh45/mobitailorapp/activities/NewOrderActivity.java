package com.adarsh45.mobitailorapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adarsh45.mobitailorapp.R;
import com.adarsh45.mobitailorapp.pojo.Customer;
import com.adarsh45.mobitailorapp.pojo.Order;
import com.adarsh45.mobitailorapp.pojo.OrderItem;
import com.adarsh45.mobitailorapp.utils.LanguageHelper;
import com.adarsh45.mobitailorapp.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class NewOrderActivity extends AppCompatActivity {

    private static final String TAG = "NewOrderActivity";

    private LinearLayout rootViewNewOrder;
    private TextView customerNameNewOrder, customerMobileNewOrder;

//    EditText item1, item2, item3, item4;
    EditText quant1, quant2, quant3, quant4;
    EditText rate1, rate2, rate3, rate4;
    EditText datePicker;
    EditText total1, total2, total3, total4;
    TextView finalTotal, pendingAmount;
    EditText advanceAmount;
    Spinner spItem1, spItem2, spItem3, spItem4;
    LinearLayout progressLayout;

    Button btnSaveNewOrder, btnCancelNewOrder;

    int intTotal1 = 0, intTotal2 = 0,intTotal3 = 0,intTotal4 = 0;
    int intFinalTotal = 0, intAdvance = 0, intPending = 0;

//    data from shared prefs (products & rates)
    ArrayList<String> productNames = new ArrayList<>();
    ArrayList<String> productRates = new ArrayList<>();

    private Customer customer = null;
    private Order order;
//    private byte origin;

    final Calendar myCalendar = Calendar.getInstance();

//    firebase
    private FirebaseDatabase myDB;
    private DatabaseReference rootRef, orderRef;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser currentUser = mAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        initViews();
        getProductsFromSP();

        myDB = FirebaseDatabase.getInstance();
        if (currentUser == null){
            Util.showSnackBar(rootViewNewOrder, "Something went wrong! Please restart the app!");
            return;
        }
        rootRef = myDB.getReference("Users").child(currentUser.getUid());

        getOrigin();
//        setOrderDataInTexts();

        valueChangeListener(quant1, rate1, total1);
        valueChangeListener(quant2, rate2, total2);
        valueChangeListener(quant3, rate3, total3);
        valueChangeListener(quant4, rate4, total4);

        finalValueChangeListener();
        pickDateForOrder();

        onClicks();

        LanguageHelper.updateLanguage(this);

    }

    private void getProductsFromSP() {
//        fetch data from shared prefs, add to array & show them in spinners
        productNames.add("Select Product");
        productRates.add("");
        SharedPreferences prefs = getSharedPreferences(Util.SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
        if (prefs.contains(Util.PRODUCT_NAME_1) && prefs.contains(Util.PRODUCT_RATE_1)){
            productNames.add(prefs.getString(Util.PRODUCT_NAME_1, ""));
            productRates.add(prefs.getString(Util.PRODUCT_RATE_1, ""));
        }
        if (prefs.contains(Util.PRODUCT_NAME_2) && prefs.contains(Util.PRODUCT_RATE_2)){
            productNames.add(prefs.getString(Util.PRODUCT_NAME_2, ""));
            productRates.add(prefs.getString(Util.PRODUCT_RATE_2, ""));
        }
        if (prefs.contains(Util.PRODUCT_NAME_3) && prefs.contains(Util.PRODUCT_RATE_3)){
            productNames.add(prefs.getString(Util.PRODUCT_NAME_3, ""));
            productRates.add(prefs.getString(Util.PRODUCT_RATE_3, ""));
        }
        if (prefs.contains(Util.PRODUCT_NAME_4) && prefs.contains(Util.PRODUCT_RATE_4)){
            productNames.add(prefs.getString(Util.PRODUCT_NAME_4, ""));
            productRates.add(prefs.getString(Util.PRODUCT_RATE_4, ""));
        }

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.custom_spinner_item, productNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerClickListener(spItem1, rate1);
        spinnerClickListener(spItem2, rate2);
        spinnerClickListener(spItem3, rate3);
        spinnerClickListener(spItem4, rate4);

        spItem1.setAdapter(adapter);
        spItem2.setAdapter(adapter);
        spItem3.setAdapter(adapter);
        spItem4.setAdapter(adapter);
    }

    private void spinnerClickListener(Spinner spinnerItem, EditText etRate) {
        spinnerItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                don't set text if position is 0 (i.e. select product text is there)
                if(position != 0) {
                    etRate.setText(productRates.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

//    private void setOrderDataInTexts() {
//        if (origin == Util.UPDATE_ORDER && order != null){
//            item1.setText(order.getItem_1().getItem_name());
//            item2.setText(order.getItem_2().getItem_name());
//            item3.setText(order.getItem_3().getItem_name());
//            item4.setText(order.getItem_4().getItem_name());
//
////            TextView finalTotal, pendingAmount;
////            EditText advanceAmount;
//            quant1.setText(order.getItem_1().getItem_quantity());
//            quant2.setText(order.getItem_2().getItem_quantity());
//            quant3.setText(order.getItem_3().getItem_quantity());
//            quant4.setText(order.getItem_4().getItem_quantity());
//
//            rate1.setText(order.getItem_1().getItem_rate());
//            rate2.setText(order.getItem_2().getItem_rate());
//            rate3.setText(order.getItem_3().getItem_rate());
//            rate4.setText(order.getItem_4().getItem_rate());
//
//            total1.setText(order.getItem_1().getTotal());
//            total2.setText(order.getItem_2().getTotal());
//            total3.setText(order.getItem_3().getTotal());
//            total4.setText(order.getItem_4().getTotal());
//
//            datePicker.setText(order.getDelivery_date());
//            finalTotal.setText(order.getTotal_amount());
//            pendingAmount.setText(order.getPending_amount());
//            advanceAmount.setText(order.getAdvance_amount());
//
//        }
//    }

    private void getOrigin() {
        customer = getIntent().getParcelableExtra("oldCustomer");
        order = getIntent().getParcelableExtra("oldOrder");
//        origin = getIntent().getByteExtra("origin", Util.UNKNOWN_ORIGIN);

        if (customer != null){
            customerNameNewOrder.setText(customer.getCustomerName());
            customerMobileNewOrder.setText(customer.getCustomerMobile());
        } else {
            Util.showSnackBar(rootViewNewOrder, "Something went wrong! Please restart the app!");
        }
    }

    private void onClicks() {
        btnSaveNewOrder.setOnClickListener(v -> {
            if(Util.isNetworkAvailable(NewOrderActivity.this)){
                btnSaveNewOrder.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                saveNewOrder();
            } else {
                Util.showSnackBar(rootViewNewOrder, "Device is offline, Please turn on Internet!");
            }
        });

        btnCancelNewOrder.setOnClickListener(v -> finish());
    }

//    private void updateOrder() {
//        Log.d(TAG, "updateOrder: HELLO");
//
//        if (order == null){
//            Util.showSnackBar(rootViewNewOrder, "Previous order not found! Please retry!");
//            return;
//        }
//        Order orderData = getOrderData();
//        if (orderData == null){
//            Util.showSnackBar(rootViewNewOrder, "Some data may be empty! please retry!");
//            return;
//        }
//        orderData.setOrder_id(order.getOrder_id());
//        orderData.setOrder_ref_no(order.getOrder_ref_no());
//
//        saveOrderToDB(orderData);
//
//    }

    private void saveOrderToDB(final Order orderData) {

        String customerId = customer.getCustomerID();

        orderRef = rootRef.child("Orders").child(customerId);
        //                push this order object to DB
        orderRef.child(orderData.getOrder_id()).setValue(orderData).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
//                            order details are saved successfully
//                            now update the ordersCounter value as per the latest order reference number
                    rootRef.child("Orders").child("ordersCount").setValue(orderData.getOrder_ref_no()).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            progressLayout.setVisibility(View.GONE);
                            btnSaveNewOrder.setVisibility(View.VISIBLE);
//                                        open order pdf activity with order and customer object passed and close this one
                            Intent intent = new Intent(NewOrderActivity.this, OrderPdfActivity.class);
                            intent.putExtra("customer", customer);
                            intent.putExtra("order", orderData);
                            startActivity(intent);
                            finish();
                        } else {
                            Util.showSnackBar(rootViewNewOrder, "Error updating order reference number,Kindly delete Order and recreate!");
                        }
                    });
            } else Util.showSnackBar(rootViewNewOrder, "Error saving order, Something went wrong!");
        });
    }

    private Order getOrderData(){
        Order orderData;
        if (customer == null) {
            Util.showSnackBar(rootViewNewOrder, "Something went wrong! Please restart the app!");
            return null;
        }

        String item_1_name, item_2_name, item_3_name, item_4_name;
        String item_1_quantity, item_2_quantity, item_3_quantity, item_4_quantity;
        String item_1_rate, item_2_rate, item_3_rate, item_4_rate;
        String item_1_total, item_2_total, item_3_total, item_4_total;

        OrderItem orderItem1 = new OrderItem();
        OrderItem orderItem2 = new OrderItem();
        OrderItem orderItem3 = new OrderItem();
        OrderItem orderItem4 = new OrderItem();

        if(spItem1.getSelectedItemPosition() != 0){
            item_1_name = (String) spItem1.getSelectedItem();
            item_1_quantity = quant1.getText().toString();
            item_1_rate = rate1.getText().toString();
            item_1_total = total1.getText().toString();
            orderItem1 = new OrderItem(item_1_name, item_1_quantity, item_1_rate, item_1_total);
        }
        if(spItem2.getSelectedItemPosition() != 0){
            item_2_name = (String) spItem2.getSelectedItem();
            item_2_quantity = quant2.getText().toString();
            item_2_rate = rate2.getText().toString();
            item_2_total = total2.getText().toString();
            orderItem2 = new OrderItem(item_2_name, item_2_quantity, item_2_rate, item_2_total);
        }
        if(spItem3.getSelectedItemPosition() != 0){
            item_3_name = (String) spItem3.getSelectedItem();
            item_3_quantity = quant3.getText().toString();
            item_3_rate = rate3.getText().toString();
            item_3_total = total3.getText().toString();
            orderItem3 = new OrderItem(item_3_name, item_3_quantity, item_3_rate, item_3_total);
        }
        if(spItem4.getSelectedItemPosition() != 0){
            item_4_name = (String) spItem4.getSelectedItem();
            item_4_quantity = quant4.getText().toString();
            item_4_rate = rate4.getText().toString();
            item_4_total = total4.getText().toString();
            orderItem4 = new OrderItem(item_4_name, item_4_quantity, item_4_rate, item_4_total);
        }

        if (TextUtils.isEmpty(datePicker.getText())){
            datePicker.setError("Delivery Date must be entered!");
            Util.showSnackBar(rootViewNewOrder, "Delivery Date must be entered!");
            return null;
        }
        if (TextUtils.isEmpty(finalTotal.getText())){
            finalTotal.setError("Final Total could not be empty!");
            Util.showSnackBar(rootViewNewOrder, "Final Total could not be empty!");
            return null;
        }
        if (TextUtils.isEmpty(advanceAmount.getText())){
            advanceAmount.setError("Advance Amount could not be empty!");
            Util.showSnackBar(rootViewNewOrder, "Advance Amount could not be empty!");
            return null;
        }
        if (TextUtils.isEmpty(pendingAmount.getText())){
            pendingAmount.setError("Pending Amount could not be empty!");
            Util.showSnackBar(rootViewNewOrder, "Pending Amount could not be empty!");
            return null;
        }
        final String deliveryDate = datePicker.getText().toString();
        final String totalAmount = finalTotal.getText().toString();
        final String strAdvanceAmount = advanceAmount.getText().toString();
        final String strPendingAmount = pendingAmount.getText().toString();

        String orderCreationDate = Util.getCurrentDate();

        //                creating actual order object having all the details
        orderData = new Order
                (orderCreationDate, deliveryDate, totalAmount, strAdvanceAmount, strPendingAmount,
                        orderItem1, orderItem2, orderItem3, orderItem4);

        return orderData;
    }

    private void saveNewOrder() {

        final Order orderData = getOrderData();

        String customerId = customer.getCustomerID();

        orderRef = rootRef.child("Orders").child(customerId);

        if (orderData == null){
            Util.showSnackBar(rootViewNewOrder, "Some Data might be empty! Please retry!");
            return;
        }

        final String[] ordersCount = {"0000"};

//        first fetch ordersCount counter from DB for using as order ref no
        rootRef.child("Orders").child("ordersCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
//                    order ref no is already present just fetch it and save in variable
                    ordersCount[0] = snapshot.getValue(String.class);
                    Log.d(TAG, "onComplete: GOT FROM DB");
                } else {
//                    ordersCount counter was not present in DB
//                    now creating ordersCount field in DB and saving it in local variable also
                    rootRef.child("Orders").child("ordersCount").setValue("0000").addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            ordersCount[0] = "0000";
                        } else {
                            Toast.makeText(NewOrderActivity.this, "Problem getting Order Reference No from Database!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                String orderID = orderRef.push().getKey();
//                update the ordersCount counter and use it as a order ref no
                Log.d(TAG, "onDataChange: ordersCount=" + ordersCount[0]);
                @SuppressLint("DefaultLocale") final String orderRefNo = String.format("%04d", Integer.parseInt(ordersCount[0]) + 1);

                Log.d(TAG, "onDataChange: orderRefNo:"+ orderRefNo);

                if (orderID == null){
                    Util.showSnackBar(rootViewNewOrder, "Something wrong with order! Please re-open the app!");
                    return;
                }
//                creating actual order object having all the details
                orderData.setOrder_id(orderID);
                orderData.setOrder_ref_no(orderRefNo);

//                push order to DB & proceed to next activity
                saveOrderToDB(orderData);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void pickDateForOrder() {
        datePicker.setOnClickListener(v -> {
            DatePickerDialog.OnDateSetListener datePickerListener = (view, year, month, dayOfMonth) -> {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            };
            new DatePickerDialog(
                    NewOrderActivity.this,
                    datePickerListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        datePicker.setText(sdf.format(myCalendar.getTime()));
    }

    private void initViews() {

        spItem1 = findViewById(R.id.spinner_item_1);
        spItem2 = findViewById(R.id.spinner_item_2);
        spItem3 = findViewById(R.id.spinner_item_3);
        spItem4 = findViewById(R.id.spinner_item_4);

        rootViewNewOrder = findViewById(R.id.root_view_new_order);

        customerNameNewOrder = findViewById(R.id.customer_name_new_order);
        customerMobileNewOrder = findViewById(R.id.customer_mobile_new_order);

//        item1 = findViewById(R.id.item_1);
//        item2 = findViewById(R.id.item_2);
//        item3 = findViewById(R.id.item_3);
//        item4 = findViewById(R.id.item_4);

        quant1 = findViewById(R.id.quant_1);
        quant2 = findViewById(R.id.quant_2);
        quant3 = findViewById(R.id.quant_3);
        quant4 = findViewById(R.id.quant_4);

        rate1 = findViewById(R.id.rate_1);
        rate2 = findViewById(R.id.rate_2);
        rate3 = findViewById(R.id.rate_3);
        rate4 = findViewById(R.id.rate_4);

        total1 = findViewById(R.id.total_1);
        total2 = findViewById(R.id.total_2);
        total3 = findViewById(R.id.total_3);
        total4 = findViewById(R.id.total_4);

        finalTotal = findViewById(R.id.final_total);
        advanceAmount = findViewById(R.id.advance);
        pendingAmount = findViewById(R.id.pending);

        datePicker = findViewById(R.id.new_order_date_picker);

        btnSaveNewOrder = findViewById(R.id.btn_save_new_order);
        btnCancelNewOrder = findViewById(R.id.btn_cancel_new_order);
        progressLayout = findViewById(R.id.progress_new_order);

        //    for getting back to previous activity (showing arrow)
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_new_bill);

    }

    //    for getting back to previous activity (onclick for arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void valueChangeListener(final EditText quantity, final EditText rate, final TextView total){

        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int intQuant, intRate;

                if (TextUtils.isEmpty(s)) intQuant = 0;
                else intQuant = Integer.parseInt(s.toString());

                if (TextUtils.isEmpty(rate.getText())) intRate = 0;
                else intRate = Integer.parseInt(rate.getText().toString());

                total.setText(String.valueOf(intQuant * intRate));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int intQuant, intRate;
                if (TextUtils.isEmpty(s)) intRate = 0;
                else intRate = Integer.parseInt(s.toString());

                if(TextUtils.isEmpty(quantity.getText())) intQuant = 0;
                else intQuant = Integer.parseInt(quantity.getText().toString());

                total.setText(String.valueOf(intQuant * intRate));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        total.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (total.getId()){
                    case R.id.total_1:
                        intTotal1 = Integer.parseInt(s.toString());
                        break;
                    case R.id.total_2:
                        intTotal2 = Integer.parseInt(s.toString());
                        break;
                    case R.id.total_3:
                        intTotal3 = Integer.parseInt(s.toString());
                        break;
                    case R.id.total_4:
                        intTotal4 = Integer.parseInt(s.toString());
                        break;
                }
                intFinalTotal = intTotal1+intTotal2+intTotal3+intTotal4;
                finalTotal.setText(String.valueOf(intFinalTotal));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void finalValueChangeListener() {
        finalTotal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                intPending = intFinalTotal - intAdvance;
                pendingAmount.setText(String.valueOf(intPending));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        advanceAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) intAdvance = 0;
                else intAdvance = Integer.parseInt(s.toString());

                if (intAdvance > intFinalTotal){
                    advanceAmount.setError("Advance is greater than Total bill");
                    return;
                }
                intPending = intFinalTotal - intAdvance;
                pendingAmount.setText(String.valueOf(intPending));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}