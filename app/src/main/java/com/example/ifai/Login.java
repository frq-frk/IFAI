package com.example.ifai;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText phone;
    Button login;

    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone=findViewById(R.id.phone);
        bar=findViewById(R.id.loading);
        //this functions sends the controll to verify activity along with mobile number as an parameter

        login=findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number = phone.getText().toString().trim();

                //to check if empty
                if(number.isEmpty()|| number.length() < 10){
                    phone.setError("Enter valid number");
                    phone.requestFocus();
                    return;
                }
                String phoneNumber = "+91" + number;

                Intent intent = new Intent(Login.this,Verify_phone.class);
                intent.putExtra("phonenumber", phoneNumber);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        //to check if user is existed or new user
        //to prevent login on every time
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(Login.this,upload.class);
            //to prevent onBackpressed, Security concern
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| intent.FLAG_ACTIVITY_CLEAR_TASK);//similar to no cache header in jsp
            intent.putExtra("UID",FirebaseAuth.getInstance().getCurrentUser().getUid());
            startActivity(intent);
        }
    }
}
