/**
 * Created by scottwang on 12/31/14.
 */

package xyz.getgoing.going;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Saves any missing user fields to Parse after first login
 */
public class SetProfileActivity extends ActionBarActivity {

    private EditText mGenderEditText;
    private EditText mBirthdayEditText;
    private EditText mHometownEditText;
    private Button mSaveButton;
    private ProgressBar mProgressSpinner;
    private Boolean mNoGender;
    private Boolean mNoAge;
    private Boolean mNoHometown;
    private ParseUser mCurrentUser;

    /** Displays appropriate views after checking which user fields are missing from Parse */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);
        findViews();
        mCurrentUser = ParseUser.getCurrentUser();

        // Initialize Boolean fields based on which user fields are missing
        identifyMissingFields();

        // Display appropriate views and prompt user to manually enter missing fields
        if (mNoGender) {
            setUpGender();
        }
        if (mNoAge) {
            setUpAge();
        }
        if (mNoHometown) {
            setUpHometown();
        }

        setUpSaveButton();
    }

    /** Finds views */
    private void findViews() {
        mGenderEditText = (EditText) findViewById(R.id.set_profile_gender_edittext);
        mBirthdayEditText = (EditText) findViewById(R.id.set_profile_birthday_edittext);
        mHometownEditText = (EditText) findViewById(R.id.set_profile_hometown_edittext);
        mSaveButton = (Button) findViewById(R.id.set_profile_save_button);
        mProgressSpinner = (ProgressBar) findViewById(R.id.set_profile_progress_spinner);
    }

    /** Initializes Boolean fields based on Boolean extras passed from LoginActivity */
    private void identifyMissingFields() {
        Intent intent = getIntent();
        mNoGender = intent.getExtras().getBoolean("noGender");
        mNoAge = intent.getExtras().getBoolean("noAge");
        mNoHometown = intent.getExtras().getBoolean("noHometown");
    }

    /** Displays gender views and sets up gender picker dialog */
    private void setUpGender() {
        mGenderEditText.setVisibility(View.VISIBLE);
        mGenderEditText.setInputType(InputType.TYPE_NULL);

        mGenderEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGenderDialog();
            }
        });
    }

    /** Displays age views and sets up birthday picker dialog */
    private void setUpAge() {
        mBirthdayEditText.setVisibility(View.VISIBLE);
        mBirthdayEditText.setInputType(InputType.TYPE_NULL);

        mBirthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
    }

    /** Displays hometown views */
    private void setUpHometown() {
        mHometownEditText.setVisibility(View.VISIBLE);
    }

    /** Sets up save button */
    private void setUpSaveButton() {
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressSpinner();

                // Checks if all missing fields have been added
                Boolean emptyGender = mNoGender && mGenderEditText.getText().toString().matches("");
                Boolean emptyBirthday = mNoAge && mBirthdayEditText.getText().toString().matches("");
                Boolean emptyHometown = mNoHometown && mHometownEditText.getText().toString().matches("");

                if (emptyGender || emptyBirthday || emptyHometown) {
                    // If any fields are still missing, show an AlertDialog
                    showErrorDialog(R.string.set_profile_dialog_incomplete_message);
                    hideProgressSpinner();
                } else {
                    // Otherwise, add the missing fields to ParseUser and save to Parse
                    if (mNoGender) {
                        updateUserGender();
                    }
                    if (mNoAge) {
                        updateUserAge();
                    }
                    if (mNoHometown) {
                        updateUserHometown();
                    }

                    saveToParse();
                }
            }
        });
    }

    /** Adds gender input to ParseUser */
    private void updateUserGender() {
        String gender = mGenderEditText.getText().toString();
        mCurrentUser.put(ParseConstants.KEY_GENDER, gender);
    }

    /** Adds age input to ParseUser */
    private void updateUserAge() {
        String birthday = mBirthdayEditText.getText().toString();
        try {
            String age = calculateAge(birthday);
            mCurrentUser.put(ParseConstants.KEY_AGE, age);
        } catch (ParseException e) {
            showErrorDialog(R.string.set_profile_dialog_invalid_age_message);
        }
        mCurrentUser.put(ParseConstants.KEY_BIRTHDAY, birthday);
    }

    /** Adds hometown input to ParseUser */
    private void updateUserHometown() {
        String hometown = mHometownEditText.getText().toString();
        mCurrentUser.put(ParseConstants.KEY_HOMETOWN, hometown);
    }

    /** Saves ParseUser to Parse */
    private void saveToParse() {
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                hideProgressSpinner();
                navigateToMain();
            }
        });
    }

    /** Returns a String with age calculated from birthday*/
    private String calculateAge(String birthday) throws java.text.ParseException {
        // Create two calendars with date of birthday and current date
        Date date = new SimpleDateFormat("MM/dd/yyyy").parse(birthday);
        Date now = new Date();
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(date);
        cal2.setTime(now);

        // Check if birthday has passed this year
        int factor = 0;
        if (cal2.get(Calendar.DAY_OF_YEAR) < cal1.get(Calendar.DAY_OF_YEAR)) {
            factor = -1;
        }

        // Calculate difference in years, then subtract 1 if birthday has not yet passed this year
        int age = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR) + factor;

        return String.valueOf(age);
    }

    /** Returns an AlertDialog for picking user gender */
    private void showGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.set_profile_gender_hint);
        builder.setItems(R.array.set_profile_gender, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch(which) {
                    case 0:
                        mGenderEditText.setText(R.string.set_profile_gender_female);
                        break;
                    case 1:
                        mGenderEditText.setText(R.string.set_profile_gender_male);
                }
            }
        });

        builder.create().show();
    }

    /** Returns a DatePickerDialog for picking user birthday */
    private void showDatePickerDialog() {
        GregorianCalendar cal = new GregorianCalendar();

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
                GregorianCalendar date = new GregorianCalendar();
                date.set(year, monthOfYear, dayOfMonth);
                mBirthdayEditText.setText(dateFormatter.format(date.getTime()));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    /** Returns an error dialog with given message */
    private void showErrorDialog(int messageId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageId)
                .setTitle(R.string.dialog_error_title)
                .setPositiveButton(android.R.string.ok, null);

        builder.create().show();
    }

    /** Shows progress spinner and hides save button */
    private void showProgressSpinner() {
        mSaveButton.setVisibility(View.GONE);
        mProgressSpinner.setVisibility(View.VISIBLE);
    }

    /** Hides progress spinner and shows save button */
    private void hideProgressSpinner() {
        mProgressSpinner.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.VISIBLE);
    }

    /** Navigates to MainActivity */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}