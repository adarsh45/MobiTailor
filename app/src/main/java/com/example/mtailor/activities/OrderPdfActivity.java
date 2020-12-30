package com.example.mtailor.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.example.mtailor.R;
import com.example.mtailor.pojo.Customer;
import com.example.mtailor.pojo.Order;
import com.example.mtailor.pojo.Profile;
import com.example.mtailor.utils.Util;
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
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class OrderPdfActivity extends AppCompatActivity {

//    firebase DB
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseDatabase myDB = FirebaseDatabase.getInstance();
    private DatabaseReference rootRef = myDB.getReference("Users").child(currentUser.getUid());

    private ConstraintLayout rootLayout;
    private TextView tvOrderRefNo, tvOrderCustomerName, tvOrderMobileNo,
            tvOrderTotalAmount, tvOrderAdvanceAmount, tvOrderPendingAmount;

    private static final int STORAGE_REQUEST_CODE = 11;

    private Profile shopProfile;
    private Customer mCustomer = null;
    private Order mOrder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pdf);

        initViews();
        getShopProfile();
        getCustomer();
        getOrderDetails();
        setValuesInTVs();
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
                break;
            case R.id.btn_send_bill_whatsapp:
//                get that generated pdf and send it on whatsapp
                Util.showSnackBar(rootLayout, "Sorry, Feature is under development!");
                break;
            default:
//                do nothing
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
//        pdf file name
        String fileName = mOrder.getOrder_ref_no() + "_" +mOrder.getOrder_id();
//        pdf file path
        String filePath = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                + File.separator
                + fileName
                + ".pdf";

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

//            open pdf file for previewing
//            previewPDF(filePath);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }


    }

    private void previewPDF(String filePath) {
        //            open pdf via intent
            File file = new File(filePath);
            Intent intent = new Intent(Intent.ACTION_VIEW);;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.parse(filePath), "application/pdf");
                intent = Intent.createChooser(intent, "Open File");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            startActivity(intent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    permission granted, now savePDF()
                    savePDF();
                } else {
//                    permission denied, show error
                    Util.showSnackBar(rootLayout, "Error: Permission denied!");
                }
        }
    }
}