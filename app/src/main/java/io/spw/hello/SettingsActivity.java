package io.spw.hello;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parse.ParseUser;

/**
 * Created by scottwang on 12/29/14.
 */
public class SettingsActivity extends ActionBarActivity {

    public static final String TAG = SettingsActivity.class.getSimpleName();

    protected TextView settingsName;
    protected TextView settingsAge;
    protected TextView settingsHometown;
    protected TextView settingsAgeSpread;
    protected SeekBar settingsAgeSeekBar;
    protected TextView settingsGenderSpread;
    protected SeekBar settingsGenderSeekBar;

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
        settingsHometown.setText(currentUser.getString("hometown"));

        // Set up seek bars
        settingsAgeSeekBar = (SeekBar) findViewById(R.id.settings_age_seek_bar);
        settingsAgeSpread = (TextView) findViewById(R.id.settings_age_spread_indicator);
        settingsGenderSeekBar = (SeekBar) findViewById(R.id.settings_gender_seek_bar);
        settingsGenderSpread = (TextView) findViewById(R.id.settings_gender_spread_indicator);

        settingsAgeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        settingsAgeSpread.setText(R.string.settings_age_spread_default);
                        break;
                    case 1:
                        settingsAgeSpread.setText(R.string.settings_age_spread_1);
                        break;
                    case 2:
                        settingsAgeSpread.setText(R.string.settings_age_spread_2);
                        break;
                    case 3:
                        settingsAgeSpread.setText(R.string.settings_age_spread_3);
                        break;
                    case 4:
                        settingsAgeSpread.setText(R.string.settings_age_spread_4);
                        break;
                    default:
                        settingsAgeSpread.setText(R.string.settings_age_spread_default);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        settingsGenderSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        settingsGenderSpread.setText(R.string.settings_gender_spread_default);
                        break;
                    case 1:
                        settingsGenderSpread.setText(R.string.settings_gender_spread_1);
                        break;
                    case 2:
                        settingsGenderSpread.setText(R.string.settings_gender_spread_2);
                        break;
                    default:
                        settingsGenderSpread.setText(R.string.settings_gender_spread_default);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}