package com.RockRadioGh;

/**
 * Created by IsaacBremang on 11/14/2014.
 */
import android.annotation.TargetApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MyMediaPlayerService extends Service {

    private MediaPlayer mediaPlayer = null;

    private boolean      isPlaying = false;


    private static int classID = 579; // just a number



    public static String START_PLAY = "START_PLAY";
    @Override

    public IBinder onBind(Intent intent) {

        return null;
    }


    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getBooleanExtra(START_PLAY, false)) {

            play();

        }

        return Service.START_STICKY;

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
           // mediaPlayer = MediaPlayer.create(this, R.drawable.ad_img); // change this for your file
            try{
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource("http://s10.voscast.com:7738/;stream1416328486194/1");
                mediaPlayer.prepare();
            }catch (Exception e){
                e.printStackTrace();
            }
            mediaPlayer.setLooping(true); // this will make it loop forever
            mediaPlayer.start();
            final Notification notification = mBuilder.build();
            startForeground(classID, notification);

        }

    }

    @Override

    public void onDestroy() {

        stop();

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

    /****
     * Put the playing inside a thread to prevent the whole screen from crushing on you
     */


}
