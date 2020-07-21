package com.example.ifai;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class signup extends AppCompatActivity {

    EditText username,email,DOB;
    Button signup;
    ProgressBar bar;
    String UID;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        firestore = FirebaseFirestore.getInstance();

        username=findViewById(R.id.username);
        email=findViewById(R.id.mail);
        DOB=findViewById(R.id.date);
        bar=findViewById(R.id.loading);

//        final String uname = username.getText().toString();
//        final String mail = email.getText().toString();
//        final String dob = DOB.getText().toString();

        UID = getIntent().getStringExtra("UID"); //Got it from Login page, can also get it using FirebaseAuth.getCurrentUser().getUID;

        signup=findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bar.setVisibility(View.VISIBLE);
                DocumentReference reference = firestore.collection("Users").document(UID);//See the structure in the database everything maskes sense.
                Map<String,Object> user = new HashMap<>(); //It is the most comman method used to put values to database, uses JSON format
                user.put("uname",username.getText().toString());
                user.put("email",email.getText().toString());
                user.put("DOB",DOB.getText().toString());
                reference.set(user).                                //This statement writes the data to database
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(signup.this, "Successfull registration for " + username.getText().toString(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(com.example.ifai.signup.this,upload.class);
                                intent.putExtra("UID",UID);             //sending UID so that we don't need to call FirebaseAuth instance to get UID.
                                startActivity(intent);                          //Don't know if it works
                            }
                        });
            }
        });
    }
}
