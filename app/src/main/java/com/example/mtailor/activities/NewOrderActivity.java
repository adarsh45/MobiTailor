package com.example.mtailor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mtailor.R;

public class NewOrderActivity extends AppCompatActivity {

    EditText item1, item2, item3, item4;
    EditText quant1, quant2, quant3, quant4;
    EditText rate1, rate2, rate3, rate4;
    TextView total1, total2, total3, total4;
    TextView finalTotal, pendingAmount;
    EditText advanceAmount;

    int intTotal1 = 0, intTotal2 = 0,intTotal3 = 0,intTotal4 = 0;
    int intFinalTotal = 0, intAdvance = 0, intPending = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        init();

        valueChangeListener(quant1, rate1, total1);
        valueChangeListener(quant2, rate2, total2);
        valueChangeListener(quant3, rate3, total3);
        valueChangeListener(quant4, rate4, total4);

        finalValueChangeListener();

    }

    private void init() {
        item1 = findViewById(R.id.item_1);
        item2 = findViewById(R.id.item_2);
        item3 = findViewById(R.id.item_3);
        item4 = findViewById(R.id.item_4);

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
                String totalText = "₹ "+ intFinalTotal;
                finalTotal.setText(totalText);
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
                String pending = "₹ " + intPending;
                pendingAmount.setText(pending);
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
                String pending = "₹ " + intPending;
                pendingAmount.setText(pending);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}