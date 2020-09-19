package com.vignesh.audiovideo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoView;
    private Button btnPlay;
    private TextView txtBuffering;

    private MediaController controller;

    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";
    //    private static final String VIDEO_SAMPLE = String.valueOf(R.raw.video_sample);
    private static final String VIDEO_SAMPLE = "https://developers.google.com/training/images/tacoma_narrows.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }

        videoView = findViewById(R.id.videoView);
        btnPlay = findViewById(R.id.btnPlay);
        txtBuffering = findViewById(R.id.txtBuffering);

        controller = new MediaController(this);
        videoView.setMediaController(controller);
        controller.setAnchorView(videoView);

        btnPlay.setOnClickListener(MainActivity.this);

    }

    @Override
    public void onClick(View v) {

        videoView.start();

    }

    private void initializePlayer() {

        txtBuffering.setVisibility(View.VISIBLE);

        Uri videoUri = getMedia(VIDEO_SAMPLE);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                txtBuffering.setVisibility(View.INVISIBLE);

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
        videoView.pause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PLAYBACK_TIME, videoView.getCurrentPosition());
    }
}
