package io.spw.hello;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.parse.ParseUser;

import org.json.JSONException;

/**
 * Created by scottwang on 12/29/14.
 */
public class SettingsActivity extends ActionBarActivity {

    public static final String TAG = SettingsActivity.class.getSimpleName();

    protected TextView settingsName;
    protected TextView settingsAge;
    protected TextView settingsHometown;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ParseUser currentUser = ParseUser.getCurrentUser();

        // Update settings UI
        settingsName = (TextView) findViewById(R.id.settings_name_textview);
        settingsName.setText(currentUser.getString("firstName"));

        settingsAge = (TextView) findViewById(R.id.settings_age_textview);
        settingsAge.setText(currentUser.getString("age"));

        settingsHometown = (TextView) findViewById(R.id.settings_hometown_textview);
        String hometown = "";
        try {
            if (currentUser.getJSONObject("hometown") != null) {
                hometown = currentUser.getJSONObject("hometown").getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        settingsHometown.setText(hometown);
    }
}
