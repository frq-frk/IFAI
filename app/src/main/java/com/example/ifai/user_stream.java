package com.example.ifai;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class user_stream extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    private String title,description,vidAddress,posAddress,email,uname,views;

    private TextView ttl,desc,maker,contact,mviews;

    private ImageView view;

    private MediaPlayer mediaPlayer;

    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;

    private ProgressBar buffer;

    private MediaController mediaController;



    Handler handler = new Handler();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stream);



        ttl = findViewById(R.id.ttl);
        desc = findViewById(R.id.mv_info);
        maker = findViewById(R.id.maker);
        contact = findViewById(R.id.contact);
        view = findViewById(R.id.pstr);
        mviews = findViewById(R.id.views);
        buffer = findViewById(R.id.buffering);


        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        vidAddress = getIntent().getStringExtra("film_uri");
        posAddress = getIntent().getStringExtra("poster_uri");
        email = getIntent().getStringExtra("email");
        uname = getIntent().getStringExtra("uname");
        views = getIntent().getStringExtra("views");

        mviews.setText(views);
        ttl.setText(title);
        desc.setText(description);
        String f_maker = "maker :- " + uname;
        maker.setText(f_maker);
        String m_contact = "Contact this film_maker :-" + email;
        contact.setText(m_contact);

        Picasso.get().load(posAddress).into(view);
        Picasso.get().setLoggingEnabled(true);

        vidSurface = findViewById(R.id.surfView);
        vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);
        vidSurface.setKeepScreenOn(true);

        vidSurface.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(mediaController != null){
                    mediaController.show();
                }
                return false;
            }
        });

    }


    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();

        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(vidSurface);
        handler.post(new Runnable() {
            public void run() {
                mediaController.setEnabled(true);
                mediaController.show();
            }
        });

        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                if(i == mediaPlayer.MEDIA_INFO_BUFFERING_START){
                    buffer.setVisibility(View.VISIBLE);
                }else if(i == mediaPlayer.MEDIA_INFO_BUFFERING_END){
                    buffer.setVisibility(View.INVISIBLE);
                }

                return false;
            }
        });
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

            mediaController = new MediaController(this);
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

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void releaseMediaPlayer() {
        if(mediaPlayer!= null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int i) {
        mediaPlayer.seekTo(i);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }
}
