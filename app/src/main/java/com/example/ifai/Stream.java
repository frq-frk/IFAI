package com.example.ifai;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

public class Stream extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {


    private MediaPlayer mediaPlayer;
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;
    private String vidAddress, title, posAddress, description,uid,email,uname;
    private TextView ttl,desc;
    private ImageView pstr;
    private Button delete,accept;
    private ProgressBar bar;

    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);

        ttl = findViewById(R.id.ttl);
        desc = findViewById(R.id.mv_info);
        pstr = findViewById(R.id.pstr);
        bar = findViewById(R.id.bar);

        firestore = FirebaseFirestore.getInstance();

        vidSurface = (SurfaceView) findViewById(R.id.surfView);
        vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);

        vidAddress = getIntent().getStringExtra("film_url");
        posAddress = getIntent().getStringExtra("poster_uri");
//        System.out.println(posAddress + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        uid = getIntent().getStringExtra("uid");

        ttl.setText(title);
        Picasso.get().load(posAddress).into(pstr);
        Picasso.get().setLoggingEnabled(true);
        desc.setText(description);


        //code to get the user details using the uid
        firestore.collection("Users").document(uid).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            email = documentSnapshot.getString("email");
                            uname = documentSnapshot.getString("uname");
                        }else {
                            Toast.makeText(Stream.this, "error while fetching video maker's details", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        delete = findViewById(R.id.delete);
        //code to delete the request
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure to delete the submission? you won't be able to undo it");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        bar.setVisibility(View.VISIBLE);

                        //calling a method for deletion
                        deletInfilms();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));


            }
        });

        accept = findViewById(R.id.accept);
        //code to accept the entry request
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure to stream this film?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bar.setVisibility(View.VISIBLE);
                        //calling method to insert the films into streaming node
                        insertInStreaming();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));
                dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(view.getContext(), R.color.colorAccent));

            }
        });

    }

    private void insertInStreaming() {

        DocumentReference reference = firestore.collection("streaming").document(uid + UUID.randomUUID());
        HashMap<String,Object>  film = new HashMap<>();
        film.put("film_title",title);
        film.put("description",description);
        film.put("film_uri",vidAddress);
        film.put("poster_uri",posAddress);
        film.put("email",email);
        film.put("uname",uname);
        film.put("uid",uid);
        reference.set(film).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                    //calling a method to deleted the content in films
                    deletInfilms();
            }
        });

    }

    private void deletInfilms() {
            firestore.collection("films").document(title).delete().
                    addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Stream.this, "Action successfully completed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Stream.this,Admin.class);
                            startActivity(intent);
                        }
                    });

    }


    //code to play the video

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

        mediaPlayer.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDisplay(vidHolder);
            mediaPlayer.setDataSource(vidAddress);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
