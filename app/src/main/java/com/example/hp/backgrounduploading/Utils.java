package com.example.hp.backgrounduploading;

import android.os.Handler;

/**
 * Created by deepak on 30/3/17.
 */
public class Utils {


    // this is a supporting class for the delay creation

    public interface DelayCallback{
        void afterDelay();
    }

    public static void delay(int secs, final DelayCallback delayCallback){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                delayCallback.afterDelay();
            }
        }, secs * 1000); // afterDelay will be executed after (secs*1000) milliseconds.
    }
}
