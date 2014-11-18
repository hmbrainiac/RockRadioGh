package com.RockRadioGh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class Splash extends Activity {

    /**
     * Called when the activity is first created.
     */
    ImageView play, stop, record, download_img, advert, increase_v, decrease_v;
    TextView song, download_txt;
    static boolean isRecording=false;
    static boolean isPlaying = false;
    private AudioManager myAudioManager;
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        outputFile = Environment.getExternalStorageDirectory().
        getAbsolutePath() + "/javacodegeeksRecording.3gpp";
        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myRecorder.setOutputFile(outputFile);
        enableButton();
        play();
    }

    private void enableButton() {
        play = (ImageView) findViewById(R.id.play_img_btn);
        stop = (ImageView) findViewById(R.id.stop_img_btn);
        record = (ImageView) findViewById(R.id.record_img_btn);
        download_img = (ImageView) findViewById(R.id.download_img);
        increase_v = (ImageView) findViewById(R.id.increase_volume);
        decrease_v = (ImageView) findViewById(R.id.decrease_volume);
      //  advert = (ImageView) findViewById(R.id.add_space);
        song = (TextView) findViewById(R.id.song_Playing);
      //  download_txt = (TextView) findViewById(R.id.download_txt);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        increase_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // increase volume
                myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        });


        if (isPlaying == true){
            play.setImageResource(R.drawable.playing);
            stop.setImageResource(R.drawable.to_stop);
        }

        else{
            play.setImageResource(R.drawable.to_play);
            stop.setImageResource(R.drawable.stopped);
        }



        decrease_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // decrease volume
                myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);

            }
        });

        record.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isRecording == true) {
                    isRecording = false;
                    System.out.println("Stopped");
                    record.setImageResource(R.drawable.to_record);
                    stop(view);
                }else if (isRecording == false && isPlaying == true) {
                    isRecording = true;
                    System.out.println("Recording");
                    record.setImageResource(R.drawable.recording);
                    //startRecording(getApplicationContext());
                    start(view);
                }

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                      play();
                                    }
                                }
        );

        stop.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if(isPlaying == true){
                                            isPlaying = false;
                                            play.setImageResource(R.drawable.to_play);
                                            stop.setImageResource(R.drawable.stopped);
                                            record.setImageResource(R.drawable.to_record);
                                            Intent intent = new Intent(getApplicationContext(),

                                                    MyMediaPlayerService.class);

                                            stopService(intent);

                                            //TODO
                                            //stop recording
                                            //stop playing
                                        }
                                    }
                                }
        );
        download_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO download operation
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
public void start(View view){
    try {
        myRecorder.prepare();
        myRecorder.start();
    } catch (IllegalStateException e) {
        // start:it is called before prepare()
        // prepare: it is called after start() or before setOutputFormat()
        e.printStackTrace();
    } catch (IOException e) {
        // prepare() fails
        e.printStackTrace();
    }
//    text.setText("Recording Point: Recording");
//    startBtn.setEnabled(false);
//    stopBtn.setEnabled(true);
    Toast.makeText(getApplicationContext(), "Start recording...",
            Toast.LENGTH_SHORT).show();
}
    public void stop(View view){
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder  = null;
//            stopBtn.setEnabled(false);
//            playBtn.setEnabled(true);
//            text.setText("Recording Point: Stop recording");
            Toast.makeText(getApplicationContext(), "Stop recording...",
                    Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            //  it is called before start()
            e.printStackTrace();
        } catch (RuntimeException e) {
            // no valid audio/video data has been received
            e.printStackTrace();
        }
    }

    private  void play(){
        if(isPlaying == true){
            isPlaying = false;
            play.setImageResource(R.drawable.to_play);
            stop.setImageResource(R.drawable.stopped);
            record.setImageResource(R.drawable.to_record);
            //TODO
            //stop recording
            //stop playing
            Intent intent = new Intent(getApplicationContext(),

                    MyMediaPlayerService.class);

            stopService(intent);

        }else if(isPlaying == false){
            isPlaying = true;
            play.setImageResource(R.drawable.playing);
            stop.setImageResource(R.drawable.to_stop);
            //TODO play music
            Intent intent = new Intent(getApplicationContext(),

                    MyMediaPlayerService.class);

            stopService(intent);

            intent = new Intent(getApplicationContext(),
                    MyMediaPlayerService.class);
            intent.putExtra(MyMediaPlayerService.START_PLAY, true);
            startService(intent);

        }
    }


}
