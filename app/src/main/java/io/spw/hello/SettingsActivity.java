package io.spw.hello;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by scottwang on 12/29/14.
 */
public class SettingsActivity extends ActionBarActivity {

    public static final String TAG = SettingsActivity.class.getSimpleName();

    private ParseUser currentUser;

    private TextView mNameTextView;
    private TextView mAgeTextView;
    private TextView mHometownTextView;
    private TextView mAgeSpreadTextView;
    private TextView mGenderSpreadTextView;

    private SeekBar mAgeSeekBar;
    private SeekBar mGenderSeekBar;
    private int ageSpread;
    private int genderSpread;

    private Button mSaveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_settings);
        currentUser = ParseUser.getCurrentUser();
        ageSpread = currentUser.getInt(ParseConstants.KEY_AGE_SPREAD);
        genderSpread = currentUser.getInt(ParseConstants.KEY_GENDER_SPREAD);

        findViews();
        setTextViews();

        setUpAgeSeekBar();
        setUpGenderSeekBar();
        setUpSaveButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        ageSpread = currentUser.getInt(ParseConstants.KEY_AGE_SPREAD);
        Log.d(TAG, "Parse Age Spread " + ageSpread);
        genderSpread = currentUser.getInt(ParseConstants.KEY_GENDER_SPREAD);
        Log.d(TAG, "Parse Gender Spread " + genderSpread);

        setTextViews();
        setUpAgeSeekBar();
        setUpGenderSeekBar();
    }

    private void findViews() {
        mNameTextView = (TextView) findViewById(R.id.settings_name_textview);
        mAgeTextView = (TextView) findViewById(R.id.settings_age_textview);
        mHometownTextView = (TextView) findViewById(R.id.settings_hometown_textview);
        mAgeSpreadTextView = (TextView) findViewById(R.id.settings_age_spread_indicator);
        mGenderSpreadTextView = (TextView) findViewById(R.id.settings_gender_spread_indicator);

        mAgeSeekBar = (SeekBar) findViewById(R.id.settings_age_seek_bar);
        mGenderSeekBar = (SeekBar) findViewById(R.id.settings_gender_seek_bar);

        mSaveButton = (Button) findViewById(R.id.settings_save_button);
    }

    private void setTextViews() {
        mNameTextView.setText(currentUser.getString(ParseConstants.KEY_FIRST_NAME));
        mAgeTextView.setText(currentUser.getString(ParseConstants.KEY_AGE));
        mHometownTextView.setText(currentUser.getString(ParseConstants.KEY_HOMETOWN));

        switch (ageSpread) {
            case 8:
                mAgeSpreadTextView.setText(R.string.settings_age_spread_1);
                break;
            case 7:
                mAgeSpreadTextView.setText(R.string.settings_age_spread_2);
                break;
            case 6:
                mAgeSpreadTextView.setText(R.string.settings_age_spread_3);
                break;
            case 5:
                mAgeSpreadTextView.setText(R.string.settings_age_spread_4);
                break;
            default:
                mAgeSpreadTextView.setText(R.string.settings_age_spread_default);
                break;
        }

        switch (genderSpread) {
            case 1:
                mGenderSpreadTextView.setText(R.string.settings_gender_spread_1);
                break;
            case 2:
                mGenderSpreadTextView.setText(R.string.settings_gender_spread_2);
                break;
            default:
                mGenderSpreadTextView.setText(R.string.settings_gender_spread_default);
                break;
        }
    }

    private void setUpAgeSeekBar() {
        int progress;
        switch (ageSpread) {
            case 8:
                progress = 1;
                break;
            case 7:
                progress = 2;
                break;
            case 6:
                progress = 3;
                break;
            case 5:
                progress = 4;
                break;
            default:
                progress = 0;
                break;
        }

        mAgeSeekBar.setProgress(progress);

        mAgeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 1:
                        mAgeSpreadTextView.setText(R.string.settings_age_spread_1);
                        ageSpread = 8;
                        break;
                    case 2:
                        mAgeSpreadTextView.setText(R.string.settings_age_spread_2);
                        ageSpread = 7;
                        break;
                    case 3:
                        mAgeSpreadTextView.setText(R.string.settings_age_spread_3);
                        ageSpread = 6;
                        break;
                    case 4:
                        mAgeSpreadTextView.setText(R.string.settings_age_spread_4);
                        ageSpread = 5;
                        break;
                    default:
                        mAgeSpreadTextView.setText(R.string.settings_age_spread_default);
                        ageSpread = 0;
                        break;
                }
                Log.d(TAG, "Age Spread: " + String.valueOf(ageSpread));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setUpGenderSeekBar() {
        mGenderSeekBar.setProgress(genderSpread);

        mGenderSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress) {
                    case 0:
                        mGenderSpreadTextView.setText(R.string.settings_gender_spread_default);
                        genderSpread = 0;
                        break;
                    case 1:
                        mGenderSpreadTextView.setText(R.string.settings_gender_spread_1);
                        genderSpread = 1;
                        break;
                    case 2:
                        mGenderSpreadTextView.setText(R.string.settings_gender_spread_2);
                        genderSpread = 2;
                        break;
                    default:
                        mGenderSpreadTextView.setText(R.string.settings_gender_spread_default);
                        genderSpread = 0;
                        break;
                }
                Log.d(TAG, "Gender Spread: " + String.valueOf(genderSpread));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void setUpSaveButton() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgressBarIndeterminateVisibility(true);
                currentUser.put(ParseConstants.KEY_AGE_SPREAD, ageSpread);
                currentUser.put(ParseConstants.KEY_GENDER_SPREAD, genderSpread);

                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        setProgressBarIndeterminateVisibility(false);
                        Log.d(TAG, "Saved!");
                        navigateToMain();
                    }
                });
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}