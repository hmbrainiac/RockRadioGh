package com.RockRadioGh;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Splash extends Activity {

    /**
     * Called when the activity is first created.
     */
    ImageView play, stop, record, download_img, advert, increase_v, decrease_v;
    TextView song, download_txt;
    private static boolean isRecording;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        enableButton();

    }

    private void enableButton() {
        play = (ImageView) findViewById(R.id.play_img_btn);
        stop = (ImageView) findViewById(R.id.stop_img_btn);
        record = (ImageView) findViewById(R.id.record_img_btn);
        download_img = (ImageView) findViewById(R.id.download_img);
        increase_v = (ImageView) findViewById(R.id.increase_volume);
        decrease_v = (ImageView) findViewById(R.id.decrease_volume);
        advert = (ImageView) findViewById(R.id.add_space);
        song = (TextView) findViewById(R.id.song_Playing);
        download_txt = (TextView) findViewById(R.id.download_txt);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),

                        MyMediaPlayerService.class);

                intent.putExtra(MyMediaPlayerService.START_PLAY, true);

                startService(intent);

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),

                        MyMediaPlayerService.class);

                stopService(intent);

            }
        });


        increase_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO increase volume
            }
        });


        if (isRecording == true)
            record.setImageResource(R.drawable.recording);
        else
            record.setImageResource(R.drawable.to_record);

        decrease_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO decrease volume
            }
        });

        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isRecording == true) {
                    isRecording = false;
                    record.setImageResource(R.drawable.to_record);
                }
                if (isRecording == false) {
                    isRecording = true;
                    record.setImageResource(R.drawable.recording);
                    //startRecording(getApplicationContext());
                }

            }
        });
    }

//    private void startStreamingAudio() {
//        try {
//            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
//            if (audioStreamer != null) {
//                audioStreamer.interrupt();
//            }
//            audioStreamer = new StreamingMediaPlayer(this, textStreamed, playButton, streamButton, progressBar);
//            audioStreamer.startStreaming("http://www.pocketjourney.com/downloads/pj/tutorials/audio.mp3", 1677, 214);
//            //streamButton.setEnabled(false);
//        } catch (IOException e) {
//            Log.e(getClass().getName(), "Error starting to stream audio.", e);
//        }
//
//    }
}
