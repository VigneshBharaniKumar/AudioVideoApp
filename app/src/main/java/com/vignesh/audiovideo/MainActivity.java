package com.vignesh.audiovideo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoView;
    private Button btnPlay;
    private Button btnPre, btnFwd;
    private TextView txtBuffering;
    private Button btnPlayMusic, btnPause, btnStop;

//    private MediaController controller;

    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";
    //    private static final String VIDEO_SAMPLE = String.valueOf(R.raw.video_sample);
    private static final String VIDEO_SAMPLE = "https://developers.google.com/training/images/tacoma_narrows.mp4";

    private MediaPlayer mediaPlayer;
    private static final String SAMPLE_MUSIC = String.valueOf(R.raw.sample_music);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }

        videoView = findViewById(R.id.videoView);
        btnPlay = findViewById(R.id.btnPlay);
        btnPre = findViewById(R.id.btnPre);
        btnFwd = findViewById(R.id.btnFwd);
        txtBuffering = findViewById(R.id.txtBuffering);
        btnPlayMusic = findViewById(R.id.btnPlayMusic);
        btnPause = findViewById(R.id.btnPause);
        btnStop = findViewById(R.id.btnStop);

//        controller = new MediaController(this);
//        videoView.setMediaController(controller);
//        controller.setAnchorView(videoView);

        mediaPlayer = MediaPlayer.create(this, R.raw.sample_music);

        btnPlay.setOnClickListener(MainActivity.this);
        btnPre.setOnClickListener(MainActivity.this);
        btnFwd.setOnClickListener(MainActivity.this);
        btnPlayMusic.setOnClickListener(MainActivity.this);
        btnPause.setOnClickListener(MainActivity.this);
        btnStop.setOnClickListener(MainActivity.this);

        /*Audio Media Player*/
        /*try {
            mediaPlayer.setDataSource("android.resource://" + getPackageName() + "/" + SAMPLE_MUSIC);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        mediaPlayer.prepareAsync();*/

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnPlay) {

            if (!videoView.isPlaying()) {
                btnPlay.setText("Pause");
                videoView.start();
            } else {
                btnPlay.setText("Play");
            }

        } else if (v.getId() == R.id.btnPre) {

            /*Not working*/
            videoView.seekTo(videoView.getCurrentPosition() - 10000);
            videoView.start();

        } else if (v.getId() == R.id.btnFwd) {

            /*Not working*/
            videoView.seekTo(videoView.getCurrentPosition() + 10000);
            videoView.start();

        } else if (v.getId() == R.id.btnPlayMusic) {

            mediaPlayer.start();

        } else if (v.getId() == R.id.btnPause) {

            mediaPlayer.pause();

        } else if (v.getId() == R.id.btnStop) {

            mediaPlayer.stop();
            mediaPlayer = MediaPlayer.create(this, R.raw.sample_music);

        }

    }

    private void initializePlayer() {

        txtBuffering.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.GONE);
        btnPre.setVisibility(View.GONE);
        btnFwd.setVisibility(View.GONE);
        btnPlay.setText("Play");

        Uri videoUri = getMedia(VIDEO_SAMPLE);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                txtBuffering.setVisibility(View.INVISIBLE);
                btnPlay.setVisibility(View.VISIBLE);
                btnPre.setVisibility(View.VISIBLE);
                btnFwd.setVisibility(View.VISIBLE);

                if (mCurrentPosition > 0) {
                    videoView.seekTo(mCurrentPosition);
                } else {
                    // Skipping to 1 shows the first frame of the video.
                    videoView.seekTo(1);
                }

            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, "Playback Finished", Toast.LENGTH_SHORT).show();
                videoView.seekTo(1);
            }
        });

    }

    private Uri getMedia(String mediaName) {

        if (URLUtil.isValidUrl(mediaName))
            return Uri.parse(mediaName);
        else
            return Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_sample);

    }

    private void releasePlayer() {

        videoView.stopPlayback();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCurrentPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PLAYBACK_TIME, mCurrentPosition);
    }

}
