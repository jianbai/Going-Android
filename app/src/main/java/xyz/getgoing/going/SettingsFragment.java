/**
 * Created by @author scottwang on 1/11/15.
 */

package xyz.getgoing.going;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Provides Settings page
 */
public class SettingsFragment extends ListFragment {

    private MainActivity mMainActivity;
    private TextView mProfileNameTextView;
    private TextView mProfileInfoTextView;
    private ListView mListView;
    private String[] mSettings;
    private ParseUser mCurrentUser;
    private boolean[] mAgeSettings = {
            true, false, false, false
    };
    private int mGenderSettings = 0;

    /** Initializes member variables */
    public SettingsFragment(MainActivity activity) {
        mMainActivity = activity;
        mCurrentUser = ParseUser.getCurrentUser();
    }

    /** Retrieves user info from Parse */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        findViews(rootView);

        // Retrieve profile, age and gender info from Parse
        setUpProfile();
        setUpAge();
        setUpGender();

        return rootView;
    }

    /** Sets up settings ListView */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSettings = getResources().getStringArray(R.array.settings_items);
        mListView = getListView();

        setUpSettings();
    }

    /** Handles click events for each cell in settings ListView */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        switch(position) {
            // Age preferences
            case 0:
                showAgePreferencesDialog();
                break;
            // Gender preferences
            case 1:
                showGenderPreferencesDialog();
                break;
            // FAQ
            case 2:
                showFaqDialog();
                break;
            // RAQ
            case 3:
                showRaqDialog();
                break;
            // Report a bug
            case 4:
                showBugDialog();
                break;
            // Get in touch
            case 5:
                showContactDialog();
                break;
            // Logout
            case 6:
                logOut();
                break;
        }
    }

    /** Finds views */
    private void findViews(View rootView) {
        mProfileNameTextView = (TextView) rootView.findViewById(R.id.settings_profile_name);
        mProfileInfoTextView = (TextView) rootView.findViewById(R.id.settings_profile_info);
    }

    /** Fills profile text views with profile info from Parse */
    private void setUpProfile() {
        mProfileNameTextView.setText(mCurrentUser.getString(ParseConstants.KEY_FIRST_NAME));
        mProfileInfoTextView.setText(mCurrentUser.getString(ParseConstants.KEY_AGE) +
                "  : :  " +
                mCurrentUser.getString(ParseConstants.KEY_HOMETOWN));
    }

    /** Updates age settings with values from Parse */
    private void setUpAge() {
        mAgeSettings[0] = mCurrentUser.getBoolean(ParseConstants.KEY_AGE_SETTINGS_0);
        mAgeSettings[1] = mCurrentUser.getBoolean(ParseConstants.KEY_AGE_SETTINGS_20);
        mAgeSettings[2] = mCurrentUser.getBoolean(ParseConstants.KEY_AGE_SETTINGS_30);
        mAgeSettings[3] = mCurrentUser.getBoolean(ParseConstants.KEY_AGE_SETTINGS_40);
    }

    /** Updates gender settings with value from Parse */
    private void setUpGender() {
        mGenderSettings = mCurrentUser.getInt(ParseConstants.KEY_GENDER_SETTINGS);
    }

    /** Fills ListView from String array resource */
    private void setUpSettings() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mListView.getContext(),
                android.R.layout.simple_list_item_1,
                mSettings
        );
        setListAdapter(adapter);
    }

    /** Shows dialog for picking age preferences */
    private void showAgePreferencesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle(getString(R.string.settings_dialog_age_title))
                .setMultiChoiceItems(R.array.settings_age, mAgeSettings,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                handleAgePreferencesClick((AlertDialog) dialog, which, isChecked);
                            }
                        })
                .setPositiveButton(getString(R.string.settings_dialog_save_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateAgePreferences();
                                saveSettingsToParse();
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null);

        builder.create().show();
    }

    /** Shows dialog for picking gender preferences */
    private void showGenderPreferencesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle(getString(R.string.settings_dialog_gender_title))
                .setSingleChoiceItems(R.array.settings_gender, mGenderSettings,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGenderSettings = which;
                            }
                        })
                .setPositiveButton(getString(R.string.settings_dialog_save_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateGenderPreferences();
                                saveSettingsToParse();
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null);

        builder.create().show();
    }

    /** Shows FAQ dialog */
    private void showFaqDialog() {
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.setContentView(R.layout.dialog_faq);
        dialog.setTitle(R.string.settings_dialog_faq_title);
        Button button = (Button) dialog.findViewById(R.id.dialog_faq_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /** Shows RAQ dialog */
    private void showRaqDialog() {
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.setContentView(R.layout.dialog_raq);
        dialog.setTitle(R.string.settings_dialog_raq_title);
        Button button = (Button) dialog.findViewById(R.id.dialog_raq_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /** Shows bug dialog */
    private void showBugDialog() {
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.setContentView(R.layout.dialog_bug);
        dialog.setTitle(R.string.settings_dialog_bug_title);
        final EditText inputText = (EditText) dialog.findViewById(R.id.dialog_bug_input_edittext);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_bug_cancel_button);
        Button reportButton = (Button) dialog.findViewById(R.id.dialog_bug_report_button);

        setUpNegativeButton(dialog, inputText, cancelButton);
        setUpPositiveButton(dialog, inputText, reportButton, ParseConstants.KEY_BUG_REPORTS);

        dialog.show();
    }

    /** Shows contact dialog */
    private void showContactDialog() {
        final Dialog dialog = new Dialog(mMainActivity);
        dialog.setContentView(R.layout.dialog_contact);
        dialog.setTitle(R.string.settings_dialog_contact_title);
        final EditText inputText = (EditText) dialog.findViewById(R.id.dialog_contact_input);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_contact_cancel_button);
        Button sendButton = (Button) dialog.findViewById(R.id.dialog_contact_send_button);

        setUpNegativeButton(dialog, inputText, cancelButton);
        setUpPositiveButton(dialog, inputText, sendButton, ParseConstants.KEY_CONTACT_US);

        dialog.show();
    }

    /** Closes Facebook session, logs out of Parse and navigates to LoginActivity */
    private void logOut() {
        if(ParseFacebookUtils.getSession()!=null)
            ParseFacebookUtils.getSession().closeAndClearTokenInformation();

        ParseUser.logOut();
        navigateToLogin();
    }

    /** Provides logic for handling clicks in age preferences dialog */
    private void handleAgePreferencesClick(AlertDialog dialog, int which, boolean isChecked) {
        // If user checks an item
        if (isChecked) {
            // and it is the first item,
            if (which == 0) {
                // add first item to list
                mAgeSettings[which] = true;
                // uncheck other items
                for (int i=1; i<4; i++) {
                    mAgeSettings[i] = false;
                    dialog.getListView().setItemChecked(i, false);
                }
            } else {
                // otherwise uncheck first item
                mAgeSettings[0] = false;
                dialog.getListView().setItemChecked(0, false);
                // and add checked item to list
                mAgeSettings[which] = true;
            }
            // Else if user unchecks an item in the list
        } else {
            // uncheck the item
            mAgeSettings[which] = false;
            // check if any items are checked
            boolean noneChecked = true;
            for (boolean b : mAgeSettings) {
                if (b) noneChecked = false;
            }
            if (noneChecked) {
                // if none are checked, check the first item
                mAgeSettings[0] = true;
                dialog.getListView().setItemChecked(0, true);
            }
        }
    }

    /** Adds age preferences to ParseUser */
    private void updateAgePreferences() {
        mCurrentUser.put(ParseConstants.KEY_AGE_SETTINGS_0, mAgeSettings[0]);
        mCurrentUser.put(ParseConstants.KEY_AGE_SETTINGS_20, mAgeSettings[1]);
        mCurrentUser.put(ParseConstants.KEY_AGE_SETTINGS_30, mAgeSettings[2]);
        mCurrentUser.put(ParseConstants.KEY_AGE_SETTINGS_40, mAgeSettings[3]);
    }

    /** Adds gender preferences to ParseUser */
    private void updateGenderPreferences() {
        mCurrentUser.put(ParseConstants.KEY_GENDER_SETTINGS, mGenderSettings);
    }

    /** Saves preferences to Parse */
    private void saveSettingsToParse() {
        mCurrentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(mMainActivity, R.string.settings_toast_save_successful, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Sets up cancel button for custom dialogs */
    private void setUpNegativeButton(
            final Dialog dialog, final EditText inputText, Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardAndDismiss(inputText, dialog);
            }
        });
    }

    /** Sets up positive button for custom dialogs */
    private void setUpPositiveButton(
            final Dialog dialog, final EditText inputText, Button button, final String parseKey) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputText.getText().toString();
                if (!input.equals("")) {
                    mCurrentUser.add(parseKey, input);
                    mCurrentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Toast.makeText(mMainActivity, R.string.settings_toast_message_received, Toast.LENGTH_SHORT).show();
                        }
                    });

                    hideKeyboardAndDismiss(inputText, dialog);
                }
            }
        });
    }

    /** Hides keyboard and dismisses dialog */
    private void hideKeyboardAndDismiss(EditText inputText, Dialog dialog) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) mMainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);

        dialog.dismiss();
    }

    /** Navigates to LoginActivity */
    private void navigateToLogin() {
        Intent intent = new Intent(mMainActivity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        mMainActivity.finish();
    }

}
