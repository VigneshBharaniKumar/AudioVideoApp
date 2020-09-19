package com.vignesh.audiovideo;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private VideoView videoView;
    private Button btnPlay;
    private Button btnPre, btnFwd;
    private TextView txtBuffering;
    private Button btnPlayMusic, btnPause, btnStop;
    private SeekBar seekBarVolume;
    private SeekBar seekBarMusic;

//    private MediaController controller;

    private int mCurrentPosition = 0;
    private static final String PLAYBACK_TIME = "play_time";
    //    private static final String VIDEO_SAMPLE = String.valueOf(R.raw.video_sample);
    private static final String VIDEO_SAMPLE = "https://developers.google.com/training/images/tacoma_narrows.mp4";

    private MediaPlayer mediaPlayer;
    private static final String SAMPLE_MUSIC = String.valueOf(R.raw.sample_music);

    private AudioManager audioManager;
    private Timer timer;

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
        seekBarVolume = findViewById(R.id.seekBarVolume);
        seekBarMusic = findViewById(R.id.seekBarMusic);

//        controller = new MediaController(this);
//        videoView.setMediaController(controller);
//        controller.setAnchorView(videoView);

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int maxSystemVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentSystemVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekBarVolume.setMax(maxSystemVolume);
        seekBarVolume.setProgress(currentSystemVolume);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.sample_music);
        seekBarMusic.setMax(mediaPlayer.getDuration());
        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBarMusic.getProgress());
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.seekTo(0);
                timer.cancel();
            }
        });

        btnPlay.setOnClickListener(MainActivity.this);
        btnPre.setOnClickListener(MainActivity.this);
        btnFwd.setOnClickListener(MainActivity.this);
        btnPlayMusic.setOnClickListener(MainActivity.this);
        btnPause.setOnClickListener(MainActivity.this);
        btnStop.setOnClickListener(MainActivity.this);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btnPlay) {

            if (!videoView.isPlaying()) {
                btnPlay.setText("Pause");
                videoView.start();
            } else {
                btnPlay.setText("Play");
                videoView.pause();
            }

        } else if (v.getId() == R.id.btnPre) {

            videoView.seekTo(videoView.getCurrentPosition() - 10000);

        } else if (v.getId() == R.id.btnFwd) {

            videoView.seekTo(videoView.getCurrentPosition() + 10000);

        } else if (v.getId() == R.id.btnPlayMusic) {

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    seekBarMusic.setProgress(mediaPlayer.getCurrentPosition());
                }
            }, 0, 100);

            mediaPlayer.start();

        } else if (v.getId() == R.id.btnPause) {

            timer.cancel();
            mediaPlayer.pause();

        } else if (v.getId() == R.id.btnStop) {

            timer.cancel();
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
