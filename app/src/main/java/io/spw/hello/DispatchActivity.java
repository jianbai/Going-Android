package io.spw.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/**
 * Created by scottwang on 12/28/14.
 */
public class DispatchActivity extends Activity {

    private final int LAUNCH_DISPLAY_LENGTH = 2000;
    private ParseUser currentUser;

    public DispatchActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch);


//        ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
//
//        if (currentInstallation.getBoolean(ParseConstants.KEY_INSTALLATION_LOGGED_IN)) {
//
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                currentUser = ParseUser.getCurrentUser();

                if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
                    // Start an intent for main activity
                    startActivity(new Intent(DispatchActivity.this, MainActivity.class));
                } else {
                    // Start an intent for login activity
                    startActivity(new Intent(DispatchActivity.this, LoginActivity.class));
                }
            }
        }, LAUNCH_DISPLAY_LENGTH);

    }

}