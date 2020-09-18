package com.vignesh.audiovideo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoView;
    private Button btnPlay;

    private MediaController controller;

    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }

        videoView = findViewById(R.id.videoView);
        btnPlay = findViewById(R.id.btnPlay);

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

        Uri videoUri = getMedia();
        videoView.setVideoURI(videoUri);

        if (mCurrentPosition > 0) {
            videoView.seekTo(mCurrentPosition);
        } else {
            // Skipping to 1 shows the first frame of the video.
            videoView.seekTo(1);
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(MainActivity.this, "Playback Finished", Toast.LENGTH_SHORT).show();
                videoView.seekTo(1);
            }
        });

    }

    private Uri getMedia () {

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
