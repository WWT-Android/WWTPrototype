package joli.wwtprototype;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
/**
 * Created by Jasmine on 5/11/2016.
 * Source code tutorial: http://www.androidhive.info/2013/07/how-to-implement-android-splash-screen-2/
 */
public class SplashScreen extends Activity{

    /* Splash Screen timer */
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}