package io.spw.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

/**
 * Created by scottwang on 12/28/14.
 */
public class DispatchActivity extends Activity {

    public DispatchActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseUser currentUser = ParseUser.getCurrentUser();

        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            // Start an intent for main activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // Start an intent for login activity
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

}