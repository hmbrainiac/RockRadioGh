package com.RockRadioGh;

/**
 * Created by IsaacBremang on 11/14/2014.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import android.support.v4.app.NotificationCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.util.List;

public class MyMediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPausedinCall = false;
    private static String nowPlaying = "";
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private boolean      isPlaying = false;

    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;



    private static int classID = 1; // just a number
    private int headSetSwitch = 1;

    public static final String BROADCAST_BUFFER = "com.RockRadioGh.BroadCastBUFFER";
    public static String START_PLAY = "START_PLAY";
    Intent bufferIntent;
    public static final String  Playing_String = "com.RockRadioGh.PlayingString";
    Intent playingIntent;

    @Override
    public void onCreate(){
        bufferIntent = new Intent(BROADCAST_BUFFER);
        playingIntent = new Intent(Playing_String);
        //mediaPlayer  =
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.reset();
        registerReceiver(headsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
    }


    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener(){

            @Override
            public void onCallStateChanged(int state, String incomingNumber){
                switch (state){
                    case  TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if(mediaPlayer != null){
                            //mediaPlayer.pause();
                            stop();
                            isPausedinCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if(mediaPlayer != null){
                            play();
                            isPausedinCall = false;
                        }
                        break;


                }

            }
        };
        telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_CALL_STATE);


        if (intent.getBooleanExtra(START_PLAY, false)) {
            play();
        }
        return Service.START_STICKY;

    }

    private BroadcastReceiver headsetReceiver = new BroadcastReceiver() {
        private boolean headSetConnected = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("state")){
                if( headSetConnected && (intent.getIntExtra("states",0) == 0)){
                    headSetConnected = false;
                    headSetSwitch = 0;
                }
            }else if (!headSetConnected && (intent.getIntExtra("states",0) == 1)){
                headSetConnected = true;
                headSetSwitch = 1;
            }


            switch (headSetSwitch){
                case (0):
                    headSetDisconnected();
                    break;

                case (1):
                    break;
            }

        }



    };


   private void headSetDisconnected(){
       stop();
       stopSelf();
   }

    private void play() {

        if (!isPlaying) {

            isPlaying = true;
            Intent intent = new Intent(this, Splash.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|

                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setContentTitle("Rock Radio Gh");
            mBuilder.setContentText("Test Transmission")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentIntent(pi)
                    .build();

            mediaPlayer.reset();
            try{
                mediaPlayer.setDataSource("http://s10.voscast.com:7738/;stream1416485207845/1");


                mediaPlayer.prepareAsync();
                sendBufferingBroadcast();
               // mediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
          //  mediaPlayer.setLooping(true); // this will make it loop forever

            final Notification notification = mBuilder.build();
            startForeground(classID, notification);

        }

    }

    @Override

    public void onDestroy() {

        stop();

        if(phoneStateListener != null){
            telephonyManager.listen(phoneStateListener,PhoneStateListener.LISTEN_NONE);
        }
        resetButtonBroadcast();
        unregisterReceiver(headsetReceiver);
        stopSelf();
    }
    private void stop() {

        if (isPlaying) {

            isPlaying = false;

            if (mediaPlayer != null) {

                mediaPlayer.release();

                mediaPlayer = null;
            }

            stopForeground(true);

        }

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        //Toast.makeText(getApplicationContext(),"Buffering",Toast.LENGTH_LONG).show();

        if(!mediaPlayer.isPlaying())
            mediaPlayer.start();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {

        return false;

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        sendBufferCompleteBroadcast();
    }

    /****
     * Put the playing inside a thread to prevent the whole screen from crushing on you
     */
    public void sendBufferingBroadcast(){
        bufferIntent.putExtra("buffering","1");
        sendBroadcast(bufferIntent);
    }

    public void resetButtonBroadcast(){
        bufferIntent.putExtra("buffering","2");
        sendBroadcast(bufferIntent);
    }
    public void sendBufferCompleteBroadcast(){
        bufferIntent.putExtra("buffering","0");
        sendBroadcast(bufferIntent);
    }
    public  void sendNowPlayingBroadcast(String playing,String download){
        playingIntent.putExtra("nowPlaying",playing);
        playingIntent.putExtra("download",download);
        sendBroadcast(playingIntent);

    }


}
