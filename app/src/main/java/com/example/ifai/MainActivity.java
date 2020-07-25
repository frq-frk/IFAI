package com.example.ifai;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class MainActivity extends AppCompatActivity {

    ImageButton upload;

    boolean doubleBackToExitPressedOnce = false, clicked = false;
    int count=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        upload = findViewById(R.id.upload_btn);

//        Button button = findViewById(R.id.clickme);
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this,Stream.class);
//                startActivity(intent);
//            }
//        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this, Login.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    //initialising the layout using id
    // assigning a click listener to the layout
    //so that on clicking 5 times it displayes a password dialogue.

    @SuppressLint("ResourceAsColor")
    public void admin_login(View view) {

        if(clicked) {
            //todo the action here
            //i.e open dialog prompting password
            final EditText edittext = new EditText(view.getContext());
            edittext.setBackgroundColor(R.color.background);
            edittext.setHint("Password");
            edittext.setFocusable(true);
            edittext.setTextColor(R.color.colorAccent);

            final AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());

            alert.setTitle("Welcome Admin");
            alert.setMessage("Enter the password");
            alert.setView(edittext);
            alert.setCancelable(true);
            alert.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String pwd = edittext.getText().toString();
                    if (pwd.equals("Jormale 2020")) {
                        Intent intent = new Intent(MainActivity.this, Admin.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //kill the dialog
                      dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();

            dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
            dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.background));
        }

                if(count == 4) {
        Toast.makeText(MainActivity.this, "you click one more time to reach admin dialog", Toast.LENGTH_SHORT).show();
        clicked = true;
        count = 1;
    }
    count += 1;
                new Handler().postDelayed(new Runnable() {

        @Override
        public void run() {
            clicked=false;
        }
    }, 2000);
    }
}
