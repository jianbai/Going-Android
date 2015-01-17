package io.spw.hello;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;

/**
 * Created by scottwang on 1/11/15.
 */
public class SettingsFragment extends ListFragment {

    public static final String TAG = SettingsFragment.class.getSimpleName();

    private Activity mainActivity;
    private ParseUser currentUser;

    private ListView mListView;
    private String[] mSettings;

    private TextView mProfileNameTextView;
    private TextView mProfileInfoTextView;
    private TextView mSettingsAgeTextView;
    private TextView mSettingsGenderTextView;
    private TextView mSettingsFaqTextView;
    private TextView mSettingsBugTextView;
    private TextView mSettingsContactTextView;
    private TextView mSettingsLogoutTextView;

    private static boolean[] mAgeSettings = {
            true, false, false, false
    };
    private static int mGenderSettings = 0;

    public SettingsFragment(Activity c) {
        mainActivity = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        Log.d(TAG, "SETTINGS VIEW CREATED");
        currentUser = MainActivity.currentUser;
        mSettings = getResources().getStringArray(R.array.settings);

        findViews(rootView);
        setTextViews();

        setUpAge();
        setUpGender();
//        setUpFaq();
//        setUpBug();
//        setUpContact();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = getListView();

        setUpSettings();
    }

    private void setUpSettings() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                mListView.getContext(),
                android.R.layout.simple_list_item_1,
                mSettings
        );
        setListAdapter(adapter);
    }

    private void findViews(View rootView) {
        mProfileNameTextView = (TextView) rootView.findViewById(R.id.settings_profile_name);
        mProfileInfoTextView = (TextView) rootView.findViewById(R.id.settings_profile_info);
//        mSettingsAgeTextView = (TextView) rootView.findViewById(R.id.settings_age);
//        mSettingsGenderTextView = (TextView) rootView.findViewById(R.id.settings_gender);
//        mSettingsFaqTextView = (TextView) rootView.findViewById(R.id.settings_faq);
//        mSettingsBugTextView = (TextView) rootView.findViewById(R.id.settings_bug);
//        mSettingsContactTextView = (TextView) rootView.findViewById(R.id.settings_contact);
//        mSettingsLogoutTextView = (TextView) rootView.findViewById(R.id.settings_logout);
    }

    private void setTextViews() {
        mProfileNameTextView.setText(currentUser.getString(ParseConstants.KEY_FIRST_NAME));
        mProfileInfoTextView.setText(currentUser.getString(ParseConstants.KEY_AGE) +
                " : : " +
                currentUser.getString(ParseConstants.KEY_HOMETOWN));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        switch(position) {
            // Age preferences
            case 0:
                onAgePreferencesClicked();
                break;
            // Gender preferences
            case 1:
                onGenderPreferencesClicked();
                break;
            // FAQ
            case 2:
                break;
            // RAQ
            case 3:
                break;
            // Report a bug
            case 4:
                break;
            // Get in touch
            case 5:
                break;
            // Logout
            case 6:
                currentUser.logOut();
                navigateToLogin();
                break;
        }
    }

    private void onAgePreferencesClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(getString(R.string.settings_age_title))
                .setMultiChoiceItems(R.array.settings_age, mAgeSettings,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                // If user checks an item
                                if (isChecked) {
                                    // and it is the first item,
                                    if (which == 0) {
                                        // add first item to list
                                        mAgeSettings[which] = true;
                                        // uncheck other items
                                        for (int i=1; i<4; i++) {
                                            mAgeSettings[i] = false;
                                            ((AlertDialog) dialog).getListView()
                                                    .setItemChecked(i, false);
                                        }
                                    } else {
                                        // otherwise uncheck first item
                                        mAgeSettings[0] = false;
                                        ((AlertDialog) dialog).getListView()
                                                .setItemChecked(0, false);
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
                                        ((AlertDialog) dialog).getListView()
                                                .setItemChecked(0, true);
                                    }
                                }
                            }
                        })
                        // Set action buttons
                .setPositiveButton(getString(R.string.settings_save_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentUser.put(ParseConstants.KEY_AGE_SETTINGS_0, mAgeSettings[0]);
                                currentUser.put(ParseConstants.KEY_AGE_SETTINGS_20, mAgeSettings[1]);
                                currentUser.put(ParseConstants.KEY_AGE_SETTINGS_30, mAgeSettings[2]);
                                currentUser.put(ParseConstants.KEY_AGE_SETTINGS_40, mAgeSettings[3]);
                                currentUser.saveInBackground();
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onGenderPreferencesClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle(getString(R.string.settings_gender_title))
                .setSingleChoiceItems(R.array.settings_gender, mGenderSettings,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGenderSettings = which;
                            }
                        })
                        // Set up action buttons
                .setPositiveButton(getString(R.string.settings_save_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentUser.put(ParseConstants.KEY_GENDER_SETTINGS, mGenderSettings);
                                currentUser.saveInBackground();
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setUpAge() {
        mAgeSettings[0] = currentUser.getBoolean(ParseConstants.KEY_AGE_SETTINGS_0);
        mAgeSettings[1] = currentUser.getBoolean(ParseConstants.KEY_AGE_SETTINGS_20);
        mAgeSettings[2] = currentUser.getBoolean(ParseConstants.KEY_AGE_SETTINGS_30);
        mAgeSettings[3] = currentUser.getBoolean(ParseConstants.KEY_AGE_SETTINGS_40);
    }

    private void setUpGender() {
        mGenderSettings = currentUser.getInt(ParseConstants.KEY_GENDER_SETTINGS);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(mainActivity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        mainActivity.finish();
    }

}
