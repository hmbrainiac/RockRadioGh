package com.RockRadioGh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
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
    //private MediaPlayer myPlayer;
    private boolean isOnline;
    private String outputFile = null;
    boolean broadcastReceiverIsRegistered;
    private ProgressDialog pdBuff = null;


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
        myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.MPEG_4);
        myRecorder.setOutputFile(outputFile);
        enableButton();

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
        play();
    }


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

    private  void play() {
        checkConnectivity();
            if(isOnline){
                if (isPlaying == true) {
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

                } else if (isPlaying == false) {
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
            }else {
                Toast.makeText(getApplicationContext(),"No internet connection detected",Toast.LENGTH_LONG).show();
            }


    }

    private void checkConnectivity(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()
                ||
                cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting()){
            isOnline = true;
        }else {
            isOnline = false;
        }
    }

    private void showPD(Intent bufferIntent){
        String bufferValue = bufferIntent.getStringExtra("buffering");
        int bufferIntValue = Integer.parseInt(bufferValue);

        switch(bufferIntValue){
            case 0:
                if(pdBuff != null){
                    pdBuff.dismiss();
                }
                break;
            case 1:
                BufferDialogue();
                break;
            case 2:
                isPlaying = false;
                play.setImageResource(R.drawable.to_play);
                stop.setImageResource(R.drawable.stopped);
                break;
        }
    }

    private void BufferDialogue(){
        pdBuff = ProgressDialog.show(Splash.this,"Buffering ..."," Contacting Our Servers ...", true);

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showPD(intent);
        }
    };




    public void onResume() {
       // activity.onResume();
        if(!broadcastReceiverIsRegistered){
            registerReceiver(broadcastReceiver,new IntentFilter(MyMediaPlayerService.BROADCAST_BUFFER));
            broadcastReceiverIsRegistered = true;
        }
        super.onResume();
    }

    public void onPause() {
       // activity.onPause();
        if(broadcastReceiverIsRegistered){
            unregisterReceiver(broadcastReceiver);
            broadcastReceiverIsRegistered = false;
        }
        super.onPause();
    }

}
