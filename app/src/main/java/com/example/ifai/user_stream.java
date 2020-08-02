package com.example.ifai;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class user_stream extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {

    private String title,description,vidAddress,posAddress,email,uname;

    private TextView ttl,desc,maker,contact;

    private ImageView view;

    private MediaPlayer mediaPlayer;

    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stream);

        ttl = findViewById(R.id.ttl);
        desc = findViewById(R.id.mv_info);
        maker = findViewById(R.id.maker);
        contact = findViewById(R.id.contact);
        view = findViewById(R.id.pstr);

        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        vidAddress = getIntent().getStringExtra("film_uri");
        posAddress = getIntent().getStringExtra("poster_uri");
        email = getIntent().getStringExtra("email");
        uname = getIntent().getStringExtra("uname");

        ttl.setText(title);
        desc.setText(description);
        String f_maker = "maker :- " + uname;
        maker.setText(f_maker);
        String m_contact = "Contact this film_maker :-" + email;
        contact.setText(m_contact);

        Picasso.get().load(posAddress).into(view);
        Picasso.get().setLoggingEnabled(true);

        vidSurface = (SurfaceView) findViewById(R.id.surfView);
        vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);

    }

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
