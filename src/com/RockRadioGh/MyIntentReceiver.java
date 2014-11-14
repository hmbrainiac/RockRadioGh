package com.RockRadioGh;

import android.content.Context;
import android.content.Intent;

/**
 * Created by IsaacBremang on 11/14/2014.
 */
public class MyIntentReceiver extends android.content.BroadcastReceiver {

    public void onReceive(Context ctx, Intent intent) {

        if (intent.getAction().equals(

                android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {

            Intent myIntent = new Intent(ctx, MyMediaPlayerService.class);

            ctx.stopService(myIntent);

        }

    }

}
