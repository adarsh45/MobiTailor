package com.adarsh45.mobitailor.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.adarsh45.mobitailor.R;
import com.adarsh45.mobitailor.activities.ShirtMeasurementActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
    private Util(){}

    public static int PHONE_CALL_PERMISSION = 1;

    public static final byte UNKNOWN_ORIGIN = -1;

    public static final byte SHOW_CUSTOMERS = 11;
    public static final byte TAKE_MEASUREMENTS = 12;
    public static final byte SHOW_ORGANIZATIONS = 13;
    public static final byte SHOW_EMPLOYEES = 14;
    public static final byte NEW_ORDER = 15;
    public static final byte SHOW_ORDERS = 16;
    public static final byte UPDATE_ORDER = 17;

    public static final byte NEW_CUSTOMER = 21;
    public static final byte NEW_ORGANIZATION = 22;
    public static final byte NEW_EMPLOYEE = 23;
    public static final byte UPDATE_CUSTOMER = 24;
    public static final byte UPDATE_ORGANIZATION = 25;
    public static final byte UPDATE_EMPLOYEE = 26;

    public static final byte CUSTOMER_MEASUREMENT = 31;
    public static final byte EMP_MEASUREMENT = 32;

    public static final String SHIRT_BOX_PATTI = "Box Patti";
    public static final String SHIRT_IN_PATTI = "In Patti";
    public static final String SHIRT_COVER_SILAI = "Cover Silai";
    public static final String SHIRT_PLAIN_SILAI = "Plain Silai";

    public static final String PANT_SIDE_POCKET = "Side Pocket";
    public static final String PANT_CROSS_POCKET = "Cross Pocket";
    public static final String PANT_PLATES_YES = "Yes";
    public static final String PANT_PLATES_NO = "No";
    public static final String PANT_SIDE_STITCH = "Side Stitch";
    public static final String PANT_SIDE_PLANE = "Side Plane";
    public static final String PANT_BOTTOM_STITCH = "Bottom Stitch";
    public static final String PANT_HAND_STITCH = "Hand Stitch";
    public static final String PANT_PATTI_POCKET = "Patti Pocket";
    public static final String PANT_BONE_POCKET = "Bone Pocket";

    public static Font getFont(float fontSize, int fontStyle, BaseColor baseColor){
        return new Font(Font.FontFamily.HELVETICA, fontSize, fontStyle, baseColor);
    }

//    public static void setLocaleLanguage(Context context,String lang){
//        Locale locale = new Locale(lang);
//        Locale.setDefault(locale);
//        Configuration configuration = new Configuration();
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            configuration.locale = locale;
//        } else {
//            configuration.setLocale(locale);
//        }
//        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
////        Toast.makeText(context, "language changed", Toast.LENGTH_SHORT).show();
//    }

    public static boolean contactExists(Context context, String number) {
        /// number is the phone number
        Uri lookupUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(number));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
        try {
            if (cur.moveToFirst()) {
                cur.close();
                return true;
            }
        } finally {
            if (cur != null)
                cur.close();
        }
        return false;
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
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, Util.PHONE_CALL_PERMISSION);
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

//    public static void checkRadio(CharSequence capturedText, CharSequence baseText, RadioButton radioButton){
//        if (capturedText.equals(baseText)){
//            radioButton.toggle();
//        }
//    }

//    public static void checkRadio(RadioGroup radioGroup, CharSequence baseText){
//        int count = radioGroup.getChildCount();
//        for (int i=0; i<count; i++){
//            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
//            CharSequence radioText = radioButton.getText();
//            if (baseText.equals(radioText)){
//                radioButton.toggle();
//                break;
//            }
//        }
//    }

    public static void checkRadio(Activity activity, RadioGroup radioGroup, String baseText){
        int count = radioGroup.getChildCount();
        int selectedId = -1;
        for (int i=0; i<count; i++){
            switch (baseText){
                case SHIRT_BOX_PATTI:
                    selectedId = R.id.radio_box_patti;
                    break;
                case SHIRT_IN_PATTI:
                    selectedId = R.id.radio_in_patti;
                    break;
                case SHIRT_COVER_SILAI:
                    selectedId = R.id.radio_cover_silai;
                    break;
                case SHIRT_PLAIN_SILAI:
                    selectedId = R.id.radio_plain_silai;
                    break;
                case PANT_SIDE_POCKET:
                    selectedId = R.id.radiobtn_pant_side_pocket;
                    break;
                case PANT_CROSS_POCKET:
                    selectedId = R.id.radiobtn_pant_cross_pocket;
                    break;
                case PANT_PLATES_YES:
                    selectedId = R.id.radio_plate_yes;
                    break;
                case PANT_PLATES_NO:
                    selectedId = R.id.radio_plate_no;
                    break;
                case PANT_SIDE_STITCH:
                    selectedId = R.id.radiobtn_side_stitch;
                    break;
                case PANT_SIDE_PLANE:
                    selectedId = R.id.radiobtn_side_plane;
                    break;
                case PANT_BOTTOM_STITCH:
                    selectedId = R.id.radiobtn_bottom_stitch;
                    break;
                case PANT_HAND_STITCH:
                    selectedId = R.id.radiobtn_hand_stitch;
                    break;
                case PANT_PATTI_POCKET:
                    selectedId = R.id.radiobtn_patti_pocket;
                    break;
                case PANT_BONE_POCKET:
                    selectedId = R.id.radiobtn_bone_pocket;
                    break;
            }
            if (selectedId != -1){
                RadioButton radioButton = activity.findViewById(selectedId);
                radioButton.toggle();
                return;
            }
        }
    }

    public static String getCurrentDate(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("en"));
        return dateFormat.format(new Date());
    }

    public static String getTextFromRadioGroup(Activity activity, RadioGroup radioGroup){
        String text = "unknown";
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if(selectedId == -1){
            text = "";
        } else {
            if (selectedId == R.id.radio_box_patti){
                text = SHIRT_BOX_PATTI;
            } else if (selectedId == R.id.radio_in_patti){
                text = SHIRT_IN_PATTI;
            } else if (selectedId == R.id.radio_cover_silai){
                text = SHIRT_COVER_SILAI;
            } else if (selectedId == R.id.radio_plain_silai){
                text = SHIRT_PLAIN_SILAI;
            } else if (selectedId == R.id.radiobtn_pant_side_pocket){
                text = PANT_SIDE_POCKET;
            } else if (selectedId == R.id.radiobtn_pant_cross_pocket){
                text = PANT_CROSS_POCKET;
            } else if (selectedId == R.id.radio_plate_yes){
                text = PANT_PLATES_YES;
            } else if (selectedId == R.id.radio_plate_no){
                text = PANT_PLATES_NO;
            } else if (selectedId == R.id.radiobtn_side_stitch){
                text = PANT_SIDE_STITCH;
            } else if (selectedId == R.id.radiobtn_side_plane){
                text = PANT_SIDE_PLANE;
            } else if (selectedId == R.id.radiobtn_bottom_stitch){
                text = PANT_BOTTOM_STITCH;
            } else if (selectedId == R.id.radiobtn_hand_stitch){
                text = PANT_HAND_STITCH;
            } else if (selectedId == R.id.radiobtn_patti_pocket){
                text = PANT_PATTI_POCKET;
            } else if (selectedId == R.id.radiobtn_bone_pocket){
                text = PANT_BONE_POCKET;
            }
            }
        Log.d("TAG", "getTextFromRadioGroup: " + text);
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

    //    check if device has network
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

}
