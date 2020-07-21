package com.example.ifai;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class Verify_phone extends AppCompatActivity {

        private String verify_id, current_uid;
        private FirebaseAuth mAuth;
        private FirebaseFirestore firestore;
        private ProgressBar bar;
        private EditText otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        bar = findViewById(R.id.loading);
        otp = findViewById(R.id.otp);

        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String phonenumber = getIntent().getStringExtra("phonenumber");
        sendVerificationCode(phonenumber);

        //verify button onClick function if auto verification fails/doesn't occured
        findViewById(R.id.verify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bar.setVisibility(View.VISIBLE);
                String code = otp.getText().toString().trim();

                if(code.isEmpty()||code.length()<6){
                   otp.setError("Enter Code");
                   otp.requestFocus();
                   return;
                }
                verifyCode(code);
            }
        });
    }

    //function for manual verification uses verify_id from mCallBack function
    //that is user entered otp is verified from input

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verify_id,code);
        signInWithCredential(credential);
    }

    //called by verifycode method to know the status of verification
    //if successfull, forwarded main activity
    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            current_uid=mAuth.getCurrentUser().getUid();//get the current user's uid from firefox authentication
                            //to do if data is not found of UID in database forward to info register.
                            firestore.collection("Users").document(current_uid).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.getResult().exists()){
                                                //forwarding to upload activity if data is found of UID in database
                                                Intent intent = new Intent(Verify_phone.this,upload.class);
                                                //to prevent onBackpressed, Security concern
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| intent.FLAG_ACTIVITY_CLEAR_TASK);//similar to no cache header in jsp
                                                intent.putExtra("UID",current_uid);
                                                startActivity(intent);
                                            }
                                            else{
                                                Intent intent = new Intent(Verify_phone.this,signup.class);
                                                //to prevent onBackpressed, Security concern
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| intent.FLAG_ACTIVITY_CLEAR_TASK);//similar to no cache header in jsp
                                                intent.putExtra("UID",current_uid);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(Verify_phone.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //this fuction is to call verify function of the firebase
    //it calls a callback function named mCallBack
    private void sendVerificationCode(String number){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    //here goes the call back function
    //it overrides three other methods
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verify_id = s; //used by Verify code method on top
        }

        //status of auto verification
        //that is sms is recognized automatically even if otp is not entered
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            bar.setVisibility(View.VISIBLE);
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null){
                otp.setText(code);
                verifyCode(code);
            }
        }

        //on failing of verification
        //displays error message as toast
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Verify_phone.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
}
