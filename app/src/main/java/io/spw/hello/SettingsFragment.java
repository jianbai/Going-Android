package io.spw.hello;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
    }

    private void setTextViews() {
        mProfileNameTextView.setText(currentUser.getString(ParseConstants.KEY_FIRST_NAME));
        mProfileInfoTextView.setText(currentUser.getString(ParseConstants.KEY_AGE) +
                "  : :  " +
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
                onFaqClicked();
                break;
            // RAQ
            case 3:
                onRaqClicked();
                break;
            // Report a bug
            case 4:
                onBugClicked();
                break;
            // Get in touch
            case 5:
                onContactClicked();
                break;
            // Logout
            case 6:
                currentUser.logOut();
                navigateToLogin();
                break;
        }
    }

    private void onContactClicked() {
        final Dialog dialog = new Dialog(mainActivity);
        dialog.setContentView(R.layout.dialog_contact);
        dialog.setTitle(R.string.dialog_contact_title);
        final EditText inputText = (EditText) dialog.findViewById(R.id.dialog_contact_input);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_contact_cancel_button);
        Button reportButton = (Button) dialog.findViewById(R.id.dialog_contact_send_button);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputText.getText().toString();
                if (!input.equals("")) {
                    currentUser.add(ParseConstants.KEY_CONTACT_US, input);
                    currentUser.saveInBackground();

                    InputMethodManager inputMethodManager =
                            (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);

                    dialog.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void onBugClicked() {
        final Dialog dialog = new Dialog(mainActivity);
        dialog.setContentView(R.layout.dialog_bug);
        dialog.setTitle(R.string.dialog_bug_title);
        final EditText inputText = (EditText) dialog.findViewById(R.id.dialog_bug_input);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialog_bug_cancel_button);
        Button reportButton = (Button) dialog.findViewById(R.id.dialog_bug_report_button);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = inputText.getText().toString();
                if (!input.equals("")) {
                    currentUser.add(ParseConstants.KEY_BUG_REPORTS, input);
                    currentUser.saveInBackground();

                    InputMethodManager inputMethodManager =
                            (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);

                    dialog.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void onRaqClicked() {
        final Dialog dialog = new Dialog(mainActivity);
        dialog.setContentView(R.layout.dialog_raq);
        dialog.setTitle(R.string.dialog_raq_title);
        Button button = (Button) dialog.findViewById(R.id.dialog_raq_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void onFaqClicked() {
        final Dialog dialog = new Dialog(mainActivity);
        dialog.setContentView(R.layout.dialog_faq);
        dialog.setTitle(R.string.dialog_faq_title);
        Button button = (Button) dialog.findViewById(R.id.dialog_faq_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
