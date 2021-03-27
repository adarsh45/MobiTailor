package com.adarsh45.mobitailor.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.preference.PreferenceManager;

import com.adarsh45.mobitailor.R;

import java.util.Locale;

public class LanguageHelper {

    private static final String TAG = "LanguageHelper";

    private static Resources updatedResources;

    private LanguageHelper(){}

    public static Resources updateLanguage(Context context){
        //        get language preference from sharedPref
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = sharedPreferences.getString(LocaleHelper.SELECTED_LANGUAGE, "en");
//        do not change anything if language is English (en)
        if (lang.equals("en")) return context.getResources();
        Log.d(TAG, "updateLanguage: " + lang);
        String activityName = context.getClass().getSimpleName();
        Log.d(TAG, "updateLanguage: "+ activityName);

        //        set locale and get resources
        Context updatedContext = LocaleHelper.setLocale(context,lang);
        Resources resources = updatedContext.getResources();
        return resources;
    }

//    private
    public static Resources updateLanguage(Activity activity){
//        get language preference from sharedPref
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String lang = sharedPreferences.getString(LocaleHelper.SELECTED_LANGUAGE, "en");
//        do not change anything if language is English (en)
        if (lang.equals("en")) return activity.getResources();
        Log.d(TAG, "updateLanguage: " + lang);
        String activityName = activity.getClass().getSimpleName();
        Log.d(TAG, "updateLanguage: "+ activityName);

//        set locale and get resources
        Context updatedContext = LocaleHelper.setLocale(activity,lang);
        updatedResources = updatedContext.getResources();

        Log.d(TAG, "updateLanguage: locale lang: " + Locale.getDefault().getLanguage());
        Log.d(TAG, "updateLanguage: locale lang: " + Locale.getDefault().getDisplayLanguage());

//        change texts according to activity name using new resources
        switch (activityName){
            case "NewCustomerActivity":
                newCustomerActivity(activity);
                break;
            case "HomeActivity":
                homeActivity(activity);
                break;
            case "LoginActivity":
                loginActivity(activity);
                break;
            case "NewEmployeeActivity":
                newEmployeeActivity(activity);
                break;
            case "NewOrderActivity":
                newOrderActivity(activity);
                break;
            case "NewOrganizationActivity":
                newOrganization(activity);
                break;
            case "OrderPdfActivity":
                orderPdfActivity(activity);
                break;
            case "PantMeasurementActivity":
                pantMeasurementActivity(activity);
                break;
            case "ProfileActivity":
                profileActivity(activity);
                break;
            case "SelectProductActivity":
                selectProductActivity(activity);
                break;
            case "SettingsActivity":
                settingsActivity(activity);
                break;
            case "ShirtMeasurementActivity":
                shirtMeasurementActivity(activity);
                break;

        }
        return updatedResources;
    }

    private static void shirtMeasurementActivity(Activity activity) {
        TextView tv;
        RadioButton rbtn;
        Button btn;
        tv = activity.findViewById(R.id.text_shirt_last_update);
        tv.setText(updatedResources.getString(R.string.last_update));
        tv = activity.findViewById(R.id.text_shirt_height);
        tv.setText(updatedResources.getString(R.string.shirt_height));
        tv = activity.findViewById(R.id.text_shirt_chest);
        tv.setText(updatedResources.getString(R.string.shirt_chest));
        tv = activity.findViewById(R.id.text_shirt_stomach);
        tv.setText(updatedResources.getString(R.string.shirt_stomach));
        tv = activity.findViewById(R.id.text_shirt_seat);
        tv.setText(updatedResources.getString(R.string.shirt_seat));
        tv = activity.findViewById(R.id.text_shirt_shoulder);
        tv.setText(updatedResources.getString(R.string.shirt_shoulder));
        tv = activity.findViewById(R.id.text_shirt_sleeves);
        tv.setText(updatedResources.getString(R.string.shirt_sleeves));
        tv = activity.findViewById(R.id.text_shirt_full);
        tv.setText(updatedResources.getString(R.string.shirt_sleeves_full));
        tv = activity.findViewById(R.id.text_shirt_cuff);
        tv.setText(updatedResources.getString(R.string.shirt_sleeves_cuff));
        tv = activity.findViewById(R.id.text_shirt_full_bicep);
        tv.setText(updatedResources.getString(R.string.shirt_bicep));
        tv = activity.findViewById(R.id.text_shirt_half);
        tv.setText(updatedResources.getString(R.string.shirt_sleeves_half));
        tv = activity.findViewById(R.id.text_shirt_half_bicep);
        tv.setText(updatedResources.getString(R.string.shirt_bicep));
        tv = activity.findViewById(R.id.text_shirt_collar);
        tv.setText(updatedResources.getString(R.string.shirt_collar));
        tv = activity.findViewById(R.id.text_shirt_front);
        tv.setText(updatedResources.getString(R.string.shirt_front));
        tv = activity.findViewById(R.id.text_shirt_front_chest);
        tv.setText(updatedResources.getString(R.string.shirt_chest));
        tv = activity.findViewById(R.id.text_shirt_front_stomach);
        tv.setText(updatedResources.getString(R.string.shirt_stomach));
        tv = activity.findViewById(R.id.text_shirt_front_seat);
        tv.setText(updatedResources.getString(R.string.shirt_seat));
        tv = activity.findViewById(R.id.text_shirt_type);
        tv.setText(updatedResources.getString(R.string.type));
        tv = activity.findViewById(R.id.text_shirt_notes);
        tv.setText(updatedResources.getString(R.string.notes));

        rbtn = activity.findViewById(R.id.radio_box_patti);
        rbtn.setText(updatedResources.getString(R.string.shirt_box_patti));
        rbtn = activity.findViewById(R.id.radio_in_patti);
        rbtn.setText(updatedResources.getString(R.string.shirt_in_patti));
        rbtn = activity.findViewById(R.id.radio_cover_silai);
        rbtn.setText(updatedResources.getString(R.string.shirt_cover_silai));
        rbtn = activity.findViewById(R.id.radio_plain_silai);
        rbtn.setText(updatedResources.getString(R.string.shirt_plain_silai));

        btn = activity.findViewById(R.id.btn_save_shirt_measurement);
        btn.setText(updatedResources.getString(R.string.save));
        btn = activity.findViewById(R.id.btn_cancel);
        btn.setText(updatedResources.getString(R.string.cancel));

        Spinner shirtType = activity.findViewById(R.id.shirt_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                        (activity,
                        android.R.layout.simple_spinner_item,
                        updatedResources.getStringArray(R.array.shirt_type_list));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shirtType.setAdapter(adapter);
    }

    private static void settingsActivity(Activity activity) {
    }

    private static void selectProductActivity(Activity activity) {
        Button btn;
        btn = activity.findViewById(R.id.shirt_measurement_btn);
        btn.setText(updatedResources.getString(R.string.shirt));
        btn = activity.findViewById(R.id.pant_measurement_btn);
        btn.setText(updatedResources.getString(R.string.pant));
    }

    private static void pantMeasurementActivity(Activity activity) {
        TextView tv;
        RadioButton rbtn;
        Button btn;
        tv = activity.findViewById(R.id.text_pant_last_update);
        tv.setText(updatedResources.getString(R.string.last_update));
        tv = activity.findViewById(R.id.text_pant_height);
        tv.setText(updatedResources.getString(R.string.pant_height));
        tv = activity.findViewById(R.id.text_pant_waist);
        tv.setText(updatedResources.getString(R.string.pant_waist));
        tv = activity.findViewById(R.id.text_pant_seat);
        tv.setText(updatedResources.getString(R.string.pant_seat));
        tv = activity.findViewById(R.id.text_pant_bottom);
        tv.setText(updatedResources.getString(R.string.pant_bottom));
        tv = activity.findViewById(R.id.text_pant_knee);
        tv.setText(updatedResources.getString(R.string.pant_knee));
        tv = activity.findViewById(R.id.text_pant_thigh);
        tv.setText(updatedResources.getString(R.string.pant_thigh));
        tv = activity.findViewById(R.id.text_pant_seat_round);
        tv.setText(updatedResources.getString(R.string.pant_seat_round));
        tv = activity.findViewById(R.id.text_pant_seat_utaar);
        tv.setText(updatedResources.getString(R.string.pant_seat_utaar));
        tv = activity.findViewById(R.id.text_pant_type);
        tv.setText(updatedResources.getString(R.string.pant_type));
        tv = activity.findViewById(R.id.text_pant_pocket);
        tv.setText(updatedResources.getString(R.string.pant_pocket));
        tv = activity.findViewById(R.id.text_pant_plates);
        tv.setText(updatedResources.getString(R.string.pant_plates));
        tv = activity.findViewById(R.id.text_pant_back_pocket);
        tv.setText(updatedResources.getString(R.string.pant_back_pocket));
        tv = activity.findViewById(R.id.text_pant_notes);
        tv.setText(updatedResources.getString(R.string.notes));

        rbtn = activity.findViewById(R.id.radiobtn_pant_side_pocket);
        rbtn.setText(updatedResources.getString(R.string.pant_side_pocket));
        rbtn = activity.findViewById(R.id.radiobtn_pant_cross_pocket);
        rbtn.setText(updatedResources.getString(R.string.pant_cross_pocket));
        rbtn = activity.findViewById(R.id.radio_plate_yes);
        rbtn.setText(updatedResources.getString(R.string.yes));
        rbtn = activity.findViewById(R.id.radio_plate_no);
        rbtn.setText(updatedResources.getString(R.string.no));
        rbtn = activity.findViewById(R.id.radiobtn_side_stitch);
        rbtn.setText(updatedResources.getString(R.string.pant_side_stitch));
        rbtn = activity.findViewById(R.id.radiobtn_side_plane);
        rbtn.setText(updatedResources.getString(R.string.pant_side_plane));
        rbtn = activity.findViewById(R.id.radiobtn_bottom_stitch);
        rbtn.setText(updatedResources.getString(R.string.pant_bottom_stitch));
        rbtn = activity.findViewById(R.id.radiobtn_hand_stitch);
        rbtn.setText(updatedResources.getString(R.string.pant_hand_stitch));
        rbtn = activity.findViewById(R.id.radiobtn_patti_pocket);
        rbtn.setText(updatedResources.getString(R.string.pant_patti_pocket));
        rbtn = activity.findViewById(R.id.radiobtn_bone_pocket);
        rbtn.setText(updatedResources.getString(R.string.pant_bone_pocket));

        btn = activity.findViewById(R.id.btn_save_pant_measurement);
        btn.setText(updatedResources.getString(R.string.save));
        btn = activity.findViewById(R.id.btn_cancel);
        btn.setText(updatedResources.getString(R.string.cancel));

        Spinner pantType = activity.findViewById(R.id.pant_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (activity,
                        android.R.layout.simple_spinner_item,
                        updatedResources.getStringArray(R.array.pant_type_list));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pantType.setAdapter(adapter);
    }

    private static void orderPdfActivity(Activity activity) {
        TextView tv;
        Button btn;
        tv = activity.findViewById(R.id.text_order_ref_no);
        tv.setText(updatedResources.getString(R.string.order_ref_no));
        tv = activity.findViewById(R.id.text_order_customer);
        tv.setText(updatedResources.getString(R.string.customer_name));
        tv = activity.findViewById(R.id.text_order_mobile);
        tv.setText(updatedResources.getString(R.string.customer_mobile));
        tv = activity.findViewById(R.id.text_order_total);
        tv.setText(updatedResources.getString(R.string.total));
        tv = activity.findViewById(R.id.text_order_advance);
        tv.setText(updatedResources.getString(R.string.advance));
        tv = activity.findViewById(R.id.text_order_pending);
        tv.setText(updatedResources.getString(R.string.pending));

        btn = activity.findViewById(R.id.btn_view_bill_pdf);
        btn.setText(updatedResources.getString(R.string.view_bill_pdf));
        btn = activity.findViewById(R.id.btn_send_bill_whatsapp);
        btn.setText(updatedResources.getString(R.string.send_bill_whatsapp));
    }

    private static void newOrganization(Activity activity) {
    }

    private static void newOrderActivity(Activity activity) {
        TextView tv;
        Button btn;
        tv = activity.findViewById(R.id.text_item);
        tv.setText(updatedResources.getString(R.string.item));
        tv = activity.findViewById(R.id.text_quantity);
        tv.setText(updatedResources.getString(R.string.quantity));
        tv = activity.findViewById(R.id.text_rate);
        tv.setText(updatedResources.getString(R.string.rate));
        tv = activity.findViewById(R.id.text_total);
        tv.setText(updatedResources.getString(R.string.total));
        tv = activity.findViewById(R.id.text_del_date);
        tv.setText(updatedResources.getString(R.string.delivery_date));
        tv = activity.findViewById(R.id.text_final_total);
        tv.setText(updatedResources.getString(R.string.total));
        tv = activity.findViewById(R.id.text_advance);
        tv.setText(updatedResources.getString(R.string.advance));
        tv = activity.findViewById(R.id.text_pending);
        tv.setText(updatedResources.getString(R.string.pending));
        btn = activity.findViewById(R.id.btn_save_new_order);
        btn.setText(updatedResources.getString(R.string.save));
        btn = activity.findViewById(R.id.btn_cancel_new_order);
        btn.setText(updatedResources.getString(R.string.cancel));
    }

    private static void newEmployeeActivity(Activity activity) {
    }

    private static void loginActivity(Activity activity) {
        TextView tv;
        EditText et;
        Button btn;
        tv = activity.findViewById(R.id.login_text);
        tv.setText(updatedResources.getString(R.string.login_text));
        tv = activity.findViewById(R.id.text_mobile_sign_in);
        tv.setText(updatedResources.getString(R.string.signin_text));
        et = activity.findViewById(R.id.edit_phone);
        et.setHint(updatedResources.getString(R.string.phone_hint));
        et = activity.findViewById(R.id.edit_otp);
        et.setHint(updatedResources.getString(R.string.otp_hint));
        btn = activity.findViewById(R.id.verify_btn);
        btn.setText(updatedResources.getString(R.string.verify));
        btn = activity.findViewById(R.id.login_btn);
        btn.setText(updatedResources.getString(R.string.login_text));
    }

    private static void profileActivity(Activity activity) {
        TextView tv;
        Button btn;
        tv = activity.findViewById(R.id.text_profile);
        tv.setText(updatedResources.getString(R.string.profile));
        tv = activity.findViewById(R.id.text_shop_name);
        tv.setText(updatedResources.getString(R.string.shop_name));
        tv = activity.findViewById(R.id.text_owner_name);
        tv.setText(updatedResources.getString(R.string.owner_name));
        tv = activity.findViewById(R.id.text_address);
        tv.setText(updatedResources.getString(R.string.address));
        btn = activity.findViewById(R.id.register_btn);
        btn.setText(updatedResources.getString(R.string.register));
    }

    private static void homeActivity(Activity activity) {
        Button btn;
        btn = activity.findViewById(R.id.show_customer_btn);
        btn.setText(updatedResources.getString(R.string.customers));
        btn = activity.findViewById(R.id.show_measurements_btn);
        btn.setText(updatedResources.getString(R.string.measurements));
        btn = activity.findViewById(R.id.new_order_btn);
        btn.setText(updatedResources.getString(R.string.new_order));
        btn = activity.findViewById(R.id.show_orders_btn);
        btn.setText(updatedResources.getString(R.string.view_orders));
    }

    private static void newCustomerActivity(Activity activity){
        //                defining tv & btns first
        TextView tv;
        Button btn;
        tv = activity.findViewById(R.id.text_customer_name);
        tv.setText(updatedResources.getString(R.string.name_of_customer));
        tv = activity.findViewById(R.id.text_customer_mobile);
        tv.setText(updatedResources.getString(R.string.customer_mobile));
        tv = activity.findViewById(R.id.text_customer_address);
        tv.setText(updatedResources.getString(R.string.customer_address));
        btn = activity.findViewById(R.id.btn_register_customer);
        btn.setText(updatedResources.getString(R.string.register));
        btn = activity.findViewById(R.id.btn_cancel_customer);
        btn.setText(updatedResources.getString(R.string.cancel));
        btn = activity.findViewById(R.id.call_btn);
        btn.setText(updatedResources.getString(R.string.call_customer));
        btn = activity.findViewById(R.id.delete_btn);
        btn.setText(updatedResources.getString(R.string.delete_customer));
    }
}
