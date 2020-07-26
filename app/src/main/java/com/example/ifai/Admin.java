package com.example.ifai;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Admin extends AppCompatActivity {

    TextView count;

    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        count = findViewById(R.id.count);

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Users").get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if(queryDocumentSnapshots.isEmpty()){
                                Toast.makeText(Admin.this, "error in getting user count", Toast.LENGTH_SHORT).show();
                            }else{
                                String c = String.valueOf(queryDocumentSnapshots.size() - 1);
                                count.setText(c);
                            }
                    }
                });

    }
}
