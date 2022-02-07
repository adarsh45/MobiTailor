package com.adarsh45.mobitailorapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.adarsh45.mobitailorapp.R;

public class ProductsDialog extends DialogFragment {

    private SharedPreferences prefs;

    private EditText etProdName1, etProdName2, etProdName3, etProdName4;
    private EditText etProdRate1, etProdRate2, etProdRate3, etProdRate4;
    private Button btnSaveProducts, btnCancelProducts;

    public ProductsDialog(){}

    @Override
    public void onAttach(@NonNull Context context) {
        prefs = this.getActivity().getSharedPreferences(Util.SETTINGS_PREF_FILE, Context.MODE_PRIVATE);
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        if(getDialog() != null){
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_products, container, false);

//        init views
        etProdName1 = view.findViewById(R.id.et_product_name_1);
        etProdName2 = view.findViewById(R.id.et_product_name_2);
        etProdName3 = view.findViewById(R.id.et_product_name_3);
        etProdName4 = view.findViewById(R.id.et_product_name_4);
        etProdRate1 = view.findViewById(R.id.et_product_rate_1);
        etProdRate2 = view.findViewById(R.id.et_product_rate_2);
        etProdRate3 = view.findViewById(R.id.et_product_rate_3);
        etProdRate4 = view.findViewById(R.id.et_product_rate_4);
        btnSaveProducts = view.findViewById(R.id.btn_save_products);
        btnCancelProducts = view.findViewById(R.id.btn_cancel_products);

        if(getDialog() != null){
            getDialog().setCancelable(false);
        }

        getSavedProducts();

//        on click listeners
        btnSaveProducts.setOnClickListener(v-> saveProducts());
        btnCancelProducts.setOnClickListener(v-> dismiss());

        return view;
    }

    private void getSavedProducts() {
        if(prefs.contains(Util.PRODUCT_NAME_1) && prefs.contains(Util.PRODUCT_RATE_1)){
            etProdName1.setText(prefs.getString(Util.PRODUCT_NAME_1, ""));
            etProdRate1.setText(prefs.getString(Util.PRODUCT_RATE_1, ""));
        }
        if(prefs.contains(Util.PRODUCT_NAME_2) && prefs.contains(Util.PRODUCT_RATE_2)){
            etProdName2.setText(prefs.getString(Util.PRODUCT_NAME_2, ""));
            etProdRate2.setText(prefs.getString(Util.PRODUCT_RATE_2, ""));
        }
        if(prefs.contains(Util.PRODUCT_NAME_3) && prefs.contains(Util.PRODUCT_RATE_3)){
            etProdName3.setText(prefs.getString(Util.PRODUCT_NAME_3, ""));
            etProdRate3.setText(prefs.getString(Util.PRODUCT_RATE_3, ""));
        }
        if(prefs.contains(Util.PRODUCT_NAME_4) && prefs.contains(Util.PRODUCT_RATE_4)){
            etProdName4.setText(prefs.getString(Util.PRODUCT_NAME_4, ""));
            etProdRate4.setText(prefs.getString(Util.PRODUCT_RATE_4, ""));
        }
    }

    private void saveProducts() {
//        show error if first row is empty
        if (TextUtils.isEmpty(etProdName1.getText()) || TextUtils.isEmpty(etProdRate1.getText())){
            etProdName1.setError("");
            etProdRate1.setError("");
            Toast.makeText(getContext(), "At least add 1 product with rate!", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Util.PRODUCT_NAME_1, etProdName1.getText().toString());
        editor.putString(Util.PRODUCT_RATE_1, etProdRate1.getText().toString());

//        save only if that row is not empty
        if (!TextUtils.isEmpty(etProdName2.getText()) && !TextUtils.isEmpty(etProdRate2.getText())){
            editor.putString(Util.PRODUCT_NAME_2, etProdName2.getText().toString());
            editor.putString(Util.PRODUCT_RATE_2, etProdRate2.getText().toString());
        }
        if (!TextUtils.isEmpty(etProdName3.getText()) && !TextUtils.isEmpty(etProdRate3.getText())){
            editor.putString(Util.PRODUCT_NAME_3, etProdName3.getText().toString());
            editor.putString(Util.PRODUCT_RATE_3, etProdRate3.getText().toString());
        }
        if (!TextUtils.isEmpty(etProdName4.getText()) && !TextUtils.isEmpty(etProdRate4.getText())){
            editor.putString(Util.PRODUCT_NAME_4, etProdName4.getText().toString());
            editor.putString(Util.PRODUCT_RATE_4, etProdRate4.getText().toString());
        }

        editor.apply();
        Toast.makeText(getContext(), "Products saved successfully!", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
