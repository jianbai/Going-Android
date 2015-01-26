/**
 * Created by @author scottwang on 12/28/14.
 */

package io.spw.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/**
 * Provides launch activity
 */
public class DispatchActivity extends Activity {

    public static final int LAUNCH_DISPLAY_LENGTH = 1600;

    /** Dispatches to appropriate activity based on whether user is logged in */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();

        // Check if user is logged in to Parse and Facebook
        if (currentUser != null && ParseFacebookUtils.isLinked(currentUser)) {
            // Start an intent for main activity
            startActivity(new Intent(DispatchActivity.this, MainActivity.class));
        } else {
            // Show launch screen, then start an intent for login activity
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(DispatchActivity.this, LoginActivity.class));
                }
            }, LAUNCH_DISPLAY_LENGTH);
        }
    }

}