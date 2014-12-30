package io.spw.hello;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by scottwang on 12/29/14.
 */
public class SettingsActivity extends ActionBarActivity {

    public static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

}
