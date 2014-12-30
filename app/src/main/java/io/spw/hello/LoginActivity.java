package io.spw.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by scottwang on 12/26/14.
 * ga0RGNYHvNM5d0SLGQfpQWAPGJ8=
 */

public class LoginActivity extends Activity {

    public static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "got to login");

        // Check if user is logged in and connected to Facebook
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            Log.d(TAG, "user already logged in");
        } else {
            Log.d(TAG, "user logged out");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    public void onLoginButtonClicked(View v) {
        // TODO: show some progress dialog

        List<String> permissions = Arrays.asList("public_profile", "email");

        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                // TODO: dismiss progress
                // some code

                // Handle callback
                if (parseUser == null) {
                    Log.d(TAG, "User cancelled Facebook login :(");
                } else if (parseUser.isNew()) {
                    Log.d(TAG, "User signed up AND logged in through Facebook :)");
                    navigateToMain();
                } else {
                    Log.d(TAG, "User logged in through Facebook :)");
                    navigateToMain();
                }
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
