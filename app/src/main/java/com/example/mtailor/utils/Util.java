package com.example.mtailor.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.material.snackbar.Snackbar;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    private Util(){}

    public static String settingsSPFileName = "SettingsSP";
    public static String booleanShowOrgSection = "showOrgSection";
    public static String appLanguage = "appLanguage";

    public static int PHONE_CALL_PERMISSION = 1;

    public static final byte SHOW_CUSTOMERS = 11;
    public static final byte TAKE_MEASUREMENTS = 12;
    public static final byte SHOW_ORGANIZATIONS = 13;
    public static final byte SHOW_EMPLOYEES = 14;
    public static final byte NEW_ORDER = 15;
    public static final byte SHOW_ORDERS = 16;

    public static final byte NEW_CUSTOMER = 21;
    public static final byte NEW_ORGANIZATION = 22;
    public static final byte NEW_EMPLOYEE = 23;
    public static final byte UPDATE_CUSTOMER = 24;
    public static final byte UPDATE_ORGANIZATION = 25;
    public static final byte UPDATE_EMPLOYEE = 26;

    public static final byte CUSTOMER_MEASUREMENT = 31;
    public static final byte EMP_MEASUREMENT = 32;


    public static Font getFont(float fontSize, int fontStyle, BaseColor baseColor){
        return new Font(Font.FontFamily.HELVETICA, fontSize, fontStyle, baseColor);
    }

    public static void setLocaleLanguage(Context context,String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.locale = locale;
        } else {
            configuration.setLocale(locale);
        }
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
//        Toast.makeText(context, "language changed", Toast.LENGTH_SHORT).show();
    }

    public static void callTheCustomer(final Context context, int editTextMobileID, int callBtnID) {
        EditText editTextMobile = ((Activity) context).findViewById(editTextMobileID);
        final String mobile = editTextMobile.getText().toString().trim();
        if (!TextUtils.isEmpty(mobile)) {
            Button callBtn = ((Activity) context).findViewById(callBtnID);
            callBtn.setVisibility(View.VISIBLE);
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + mobile));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        //    Consider calling
                        //    ActivityCompat#requestPermissions
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, Util.PHONE_CALL_PERMISSION);
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    context.startActivity(callIntent);
                }
            });
        } else {
            Toast.makeText(context, "Sorry, Mobile number not found!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void showSnackBar(View rootView, CharSequence text){
        final Snackbar snackbar = Snackbar.make(rootView,text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public static void checkRadio(CharSequence capturedText, CharSequence baseText, RadioButton radioButton){
        if (capturedText.equals(baseText)){
            radioButton.toggle();
        }
    }

    public static void checkRadio(RadioGroup radioGroup, CharSequence baseText){
        int count = radioGroup.getChildCount();
        for (int i=0; i<count; i++){
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            CharSequence radioText = radioButton.getText();
            if (baseText.equals(radioText)){
                radioButton.toggle();
                break;
            }
        }
    }

    public static String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("en"));
        return dateFormat.format(new Date());
    }

    public static String getTextFromRadioGroup(Activity activity, RadioGroup radioGroup){
        String text;
        if(radioGroup.getCheckedRadioButtonId() == -1){
            text = "";
        } else {
            text = ((RadioButton) activity.findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
        }

        return text;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}
