package com.adarsh45.mobitailorapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.adarsh45.mobitailorapp.R;
import com.adarsh45.mobitailorapp.utils.LanguageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;

    private EditText editPhone, editOTP;
    private Button verifyBtn, loginBtn;
    private ProgressBar loginProgressBar;
    private LinearLayout layoutOTP;

    public String codeSent;


    @Override
    protected void onStart() {
        super.onStart();
//        mAuth.signOut();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
//            showSnackbar("Already logged in!");
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
//            mAuth.signOut();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initialize();
        LanguageHelper.updateLanguage(this);
    }

    public void showSnackbar(CharSequence text){
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.parent_layout),text,Snackbar.LENGTH_SHORT);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void initialize() {
        //init firebase auth
        mAuth = FirebaseAuth.getInstance();

        //edit texts
        editPhone = findViewById(R.id.edit_phone);
        editOTP = findViewById(R.id.edit_otp);

        //buttons
        verifyBtn = findViewById(R.id.verify_btn);
        loginBtn = findViewById(R.id.login_btn);

        //progress bar
        loginProgressBar = findViewById(R.id.login_progress_bar);
        layoutOTP = findViewById(R.id.layout_otp);
    }

    public void onClickLogin(@NonNull View view){
        if (view.getId() == R.id.verify_btn){
            //call verification method
            verifyPhone();
        } else if (view.getId() == R.id.login_btn){
            //call login method
            loginWithCode();
        }
    }

    private void verifyPhone(){
        editPhone.setEnabled(false);
        verifyBtn.setEnabled(false);

        String phone = editPhone.getText().toString();
        if (phone.isEmpty() || phone.length()<10){
            editPhone.setError("Pleas enter valid number!");
            editPhone.setEnabled(true);
            verifyBtn.setEnabled(true);
            return;
        }
        String phoneNumber = "+91"+phone;

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallback)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

        loginProgressBar.setVisibility(View.VISIBLE);

    }

    private void loginWithCode() {

        loginProgressBar.setVisibility(View.VISIBLE);
        String codeEntered = editOTP.getText().toString();

        if (codeEntered.isEmpty()){
            editOTP.setError("Code cannot be empty");
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, codeEntered);

        signInWithPhoneCredential(credential);
    }

    //CALLBACK METHOD
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            loginProgressBar.setVisibility(View.GONE);
            signInWithPhoneCredential(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Log.d(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                showSnackbar("Invalid Request! Please contact Developers!");
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                showSnackbar("Developers SMS quota Exceeded! Kindly contact Developers!");
            }

            editPhone.setEnabled(true);
            verifyBtn.setEnabled(true);
            loginProgressBar.setVisibility(View.GONE);
            showSnackbar(e.getLocalizedMessage());
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            loginProgressBar.setVisibility(View.GONE);
            showSnackbar("Code sent successfully!");
            editPhone.setEnabled(false);
            verifyBtn.setEnabled(false);
            layoutOTP.setVisibility(View.VISIBLE);
        }

    };

    //SIGN IN METHOD
    private void signInWithPhoneCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Sign in Success
//                            showSnackbar("Login Success!");
                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                //Invalid code entered by user
                                showSnackbar("Invalid code!");
                            } else {
                                showSnackbar(task.getException().getLocalizedMessage());
                            }
                        }
                    }
                });
        loginProgressBar.setVisibility(View.GONE);
    }
}
