package com.adarsh45.mobitailorapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.adarsh45.mobitailorapp.R;
import com.adarsh45.mobitailorapp.pojo.Customer;
import com.adarsh45.mobitailorapp.pojo.Order;
import com.adarsh45.mobitailorapp.pojo.Profile;
import com.adarsh45.mobitailorapp.utils.LanguageHelper;
import com.adarsh45.mobitailorapp.utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Objects;

public class OrderPdfActivity extends AppCompatActivity {

//    firebase DB
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase myDB = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = myDB.getReference("Users").child(currentUser.getUid());

    private ConstraintLayout rootLayout;
    private TextView tvOrderRefNo, tvOrderCustomerName, tvOrderMobileNo,
            tvOrderTotalAmount, tvOrderAdvanceAmount, tvOrderPendingAmount;

    private static final int STORAGE_REQUEST_CODE = 111;
    private static final int READ_CONTACTS_REQUEST_CODE = 112;

    private Profile shopProfile;
    private Customer mCustomer = null;
    private Order mOrder = null;

    File pdfFile;
    String filePath;

    private static final String TAG = "OrderPdfActivity";

    private Resources resources;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pdf);

        initViews();
        getShopProfile();
        getCustomer();
        getOrderDetails();
        setValuesInTVs();

        resources = LanguageHelper.updateLanguage(this);
        Objects.requireNonNull(getSupportActionBar()).setTitle(resources.getString(R.string.order_details));
    }

    private void getShopProfile() {
        rootRef.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    shopProfile = snapshot.getValue(Profile.class);
                } else {
                    Util.showSnackBar(rootLayout, "Something went wrong! Please re-open the app!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Util.showSnackBar(rootLayout, error.getMessage());
            }
        });
    }

    private void setValuesInTVs() {
        if (mCustomer != null && mOrder != null){
//            set values in TVs fetched from customer and order objects
            tvOrderRefNo.setText(mOrder.getOrder_ref_no());
            tvOrderCustomerName.setText(mCustomer.getCustomerName());
            tvOrderMobileNo.setText(mCustomer.getCustomerMobile());
            tvOrderTotalAmount.setText(mOrder.getTotal_amount());
            tvOrderAdvanceAmount.setText(mOrder.getAdvance_amount());
            tvOrderPendingAmount.setText(mOrder.getPending_amount());
        }
        else Util.showSnackBar(rootLayout, "Data not found! Please restart the app!");
    }

    private void getOrderDetails() {
        mOrder = getIntent().getExtras().getParcelable("order");
        if (mOrder == null){
            Util.showSnackBar(rootLayout, "Order details not found! Kindly restart the app!");
        }
    }

    private void getCustomer() {
        mCustomer = getIntent().getExtras().getParcelable("customer");
        if (mCustomer == null){
            Util.showSnackBar(rootLayout, "Customer details not found! Kindly restart the app!");
        }

    }

    private String getPdfFilePath(){
        //        pdf file name
        String fileName = mOrder.getOrder_ref_no() + "_" +mOrder.getOrder_id();
//        pdf file path
        return this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                + File.separator
                + fileName
                + ".pdf";
    }

    private void initViews() {
//        root layout (for snack bar)
        rootLayout = findViewById(R.id.root_view_order_pdf);
//        text views
        tvOrderRefNo = findViewById(R.id.tv_order_ref_no);
        tvOrderCustomerName = findViewById(R.id.tv_order_customer_name);
        tvOrderMobileNo = findViewById(R.id.tv_order_mob_no);
        tvOrderTotalAmount = findViewById(R.id.tv_order_total_amount);
        tvOrderAdvanceAmount = findViewById(R.id.tv_order_advance_amount);
        tvOrderPendingAmount = findViewById(R.id.tv_order_pending_amount);
    }

    public void onClickOrderPdf(View view){
        switch (view.getId()){
            case R.id.btn_view_bill_pdf:
//              generate pdf and view it in pdf reader
                generateInvoice();
                previewPDF(getPdfFilePath());
                break;
            case R.id.btn_send_bill_whatsapp:
//                get that generated pdf and send it on whatsapp
                checkForSendingWhatsapp();
                break;
            default:
//                do nothing
        }
    }

    private void checkForSendingWhatsapp() {
        filePath = getPdfFilePath();
        pdfFile = new File(filePath);
//        generate new invoice in all case (so that updated order can be stored to updated invoice)
        generateInvoice();

        if (! Util.isAppInstalled(this, "com.whatsapp")){
            Util.showSnackBar(rootLayout, "Error: WhatsApp not installed on your device!");
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){
//            permission already granted
            checkOrSaveContact(mCustomer.getCustomerMobile());
        }else {
//            write contacts permission not granted already, requesting now
            ActivityCompat.requestPermissions(OrderPdfActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS_REQUEST_CODE);
        }

    }

    private void sendBillToWhatsapp(){
        String formattedNumber = "91"+mCustomer.getCustomerMobile();
        filePath = getPdfFilePath();
        pdfFile = new File(filePath);
//        check if file exists first
        if (!pdfFile.exists()){
            Util.showSnackBar(rootLayout, "File not found! Please generate pdf first!");
            return;
        }
                try{
            Log.d(TAG, "sendToWhatsApp: preparing to open whatsapp");
            Intent sendIntent = new Intent("android.intent.action.MAIN");
            sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdfFile));
//            sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(formattedNumber) + "@s.whatsapp.net");
            sendIntent.putExtra("jid",formattedNumber + "@s.whatsapp.net");
//            Log.d(TAG, "sendBillToWhatsApp: "+ formattedNumber);
            sendIntent.setPackage("com.whatsapp");
            sendIntent.setType("application/pdf");
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(sendIntent);
            Log.d(TAG, "sendToWhatsApp: Activity started!");

        } catch(Exception e) {
            Toast.makeText(this,"Error/n"+ e.getMessage(),Toast.LENGTH_SHORT).show();
            Log.d(TAG, "sendToWhatsApp: "+ e.getMessage());
        }
    }

    private void generateInvoice() {
//        check permission for Marshmallow devices and above
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
//            device is >= marshmallow now check if permission is already granted or not
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
//                permission was not already granted, now request it
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, STORAGE_REQUEST_CODE);

            } else {
//                permission is already granted , proceed
                savePDF();

            }
        } else {
//            if device is below marshmallow then no need to check for permission, directly write the file
            savePDF();
        }
    }

    private void savePDF() {
//        create document class' object
        Document document = new Document();
        String filePath = getPdfFilePath();

//        delete file if previously exists
        if (new File(filePath).exists()) {
            if (! new File(filePath).delete()){
                Util.showSnackBar(rootLayout, "Error deleting previous file! kindly regenerate PDF!");
            }
        }
        try {
//          create instance of pdfwriter class (basically save this document in storage)
            PdfWriter.getInstance(document, new FileOutputStream(filePath));

//            write contents of invoice inside pdf file
            writeInPDF(document, shopProfile, mCustomer, mOrder);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private void previewPDF(String filePath) {
//        check first if file exists
        File file = new File(filePath);
        if (!file.exists()){
            Util.showSnackBar(rootLayout, "Error opening the file!");
            return;
        }
//        open pdf via intent
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            intent.setDataAndType(fileUri,"application/pdf");
//        intent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
            Log.d(TAG, "previewPDF: activity STARTED");
        } catch (Exception e) {
            e.printStackTrace();
            Util.showSnackBar(rootLayout, "Error opening the file!");
            Log.d(TAG, "previewPDF: ERROR: "+e.getMessage());
        }
    }

    private void writeInPDF(Document document,Profile shop, Customer customer, Order order) throws DocumentException {
        if (shop == null || customer == null || order == null){
            Util.showSnackBar(rootLayout, "Problem with getting Invoice data! Please re-open the app!");
            return;
        }
        document.open();

        // setting
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addAuthor("Mobi Tailor dev");

//            fonts, colors and font sizes
        Font shopTitleFont = Util.getFont(32.0f, Font.BOLD, BaseColor.BLACK);
        Font shopAddressFont = Util.getFont( 16.0f, Font.NORMAL, BaseColor.BLACK);
        Font shopContactFont = Util.getFont(16.0f, Font.NORMAL, BaseColor.BLACK);
        Font orderDetailsRowFont = Util.getFont(13.0f, Font.NORMAL, BaseColor.WHITE);
        Font itemsDetailsFont = Util.getFont(16.0f, Font.NORMAL, BaseColor.WHITE);
        Font customerNameBOLDFont = Util.getFont(16.0f, Font.BOLD, BaseColor.BLACK);
        Font customerNameFont = Util.getFont(16.0f, Font.NORMAL, BaseColor.BLACK);

        Paragraph paragraph;
        paragraph = new Paragraph(shop.getShopName(), shopTitleFont);
        document.add(paragraph);
        paragraph = new Paragraph(shop.getShopAddress(), shopAddressFont);
        document.add(paragraph);
        paragraph = new Paragraph("Contact: "+ shop.getOwnerMobile(), shopContactFont);
        document.add(paragraph);

//            creating table row
        PdfPTable orderDetailsTable = new PdfPTable(3);
        orderDetailsTable.setWidthPercentage(100);
        orderDetailsTable.setSpacingBefore(20);
        orderDetailsTable.setSpacingAfter(20);

        String[] orderDetails = {"Order Ref No: "+ order.getOrder_ref_no(), "Order Date: "+ order.getOrder_creation_date(), "Delivery Date: "+ order.getDelivery_date()};
        for (String orderDetail : orderDetails) {
            PdfPCell cell = new PdfPCell(new Paragraph(orderDetail, orderDetailsRowFont));
            cell.setPadding(10);
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setBorder(Rectangle.NO_BORDER);

//            finally add cell to table
            orderDetailsTable.addCell(cell);
        }
//        add table to document then
        document.add(orderDetailsTable);

        Chunk customerChunk;
        customerChunk = new Chunk("Bill to: ", customerNameBOLDFont);
        document.add(customerChunk);
        customerChunk = new Chunk(customer.getCustomerName(), customerNameFont);
        document.add(customerChunk);
        customerChunk = new Chunk(customer.getCustomerMobile(), customerNameFont);
        document.add(new Paragraph(customerChunk));

//            order items details table
        PdfPTable itemsTable = new PdfPTable(4);
        itemsTable.setWidthPercentage(100);
        itemsTable.setSpacingBefore(20);
        itemsTable.setSpacingAfter(20);

//            cells for items
        String[] tableItems = {
                "Items", "Quant", "Rate", "Amount",
                order.getItem_1().getItem_name(), order.getItem_1().getItem_quantity(), order.getItem_1().getItem_rate(),order.getItem_1().getTotal(),
                order.getItem_2().getItem_name(), order.getItem_2().getItem_quantity(), order.getItem_2().getItem_rate(),order.getItem_2().getTotal(),
                order.getItem_3().getItem_name(), order.getItem_3().getItem_quantity(), order.getItem_3().getItem_rate(),order.getItem_3().getTotal(),
                order.getItem_4().getItem_name(), order.getItem_4().getItem_quantity(), order.getItem_4().getItem_rate(),order.getItem_4().getTotal(),
                "", "", "Total",order.getTotal_amount(),
                "","","Advance", order.getAdvance_amount(),
                "","", "Pending",order.getPending_amount()};

        for (int i=0; i< tableItems.length; i++){
            PdfPCell cell = new PdfPCell(new Paragraph(tableItems[i], shopAddressFont));
            cell.setPadding(10);
            if (i<=3 || i==22 || i==23 || i==26 || i==27 || i==30 || i==31){
                cell = new PdfPCell(new Paragraph(tableItems[i], itemsDetailsFont));
                cell.setBackgroundColor(BaseColor.DARK_GRAY);
                cell.setPadding(10);
            }
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            itemsTable.addCell(cell);
        }

        document.add(itemsTable);

        document.close();

        Util.showSnackBar(rootLayout, "Invoice created successfully!");
    }


    public void checkOrSaveContact(String phoneNumber){
        boolean isContactExists = Util.contactExists(this, phoneNumber);
        if (!isContactExists){
//            contact doesn't exist already , save it now
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.NAME, "(Customer) " + mCustomer.getCustomerName());
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
            startActivity(intent);
        } else {
//            contact already exists
            Util.showSnackBar(rootLayout, "Contact already exists");
            sendBillToWhatsapp();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    permission granted
            switch (requestCode){
                case STORAGE_REQUEST_CODE:
                    savePDF();
                    break;
                case READ_CONTACTS_REQUEST_CODE:
                    checkOrSaveContact(mCustomer.getCustomerMobile());
                    break;
            }
        } else {
//                    permission denied, show error
            Util.showSnackBar(rootLayout, "Error: Permission denied!");
        }

    }
}