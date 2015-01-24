package io.spw.hello;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

// TODO: Refactor 1. Comments 2. Spacing 3. Variable names 4. Constant names

public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    protected static final ParseUser currentUser = ParseUser.getCurrentUser();
    protected static final Firebase currentUserRef = new Firebase(FirebaseConstants.URL_USERS).child(currentUser.getObjectId());

    public static boolean active = false;
    public boolean matchDialogSeen;
    public boolean pickFriendsDialogSeen;

    private SlidingTabLayout mSlidingTabLayout;
    public ViewPager mViewPager;
    private MainPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInstallation.getCurrentInstallation()
                .put(ParseConstants.KEY_INSTALLATION_USER_ID,
                        currentUser.getObjectId());
        ParseInstallation.getCurrentInstallation()
                .put(ParseConstants.KEY_INSTALLATION_USER_NAME,
                        currentUser.getString(ParseConstants.KEY_FIRST_NAME));
        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "INST SAVED");
                } else {
                    Log.d(TAG, e.getLocalizedMessage());
                }
            }
        });

        findViews();
        setUpSlidingTabLayout();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        matchDialogSeen = currentUser.getBoolean(ParseConstants.KEY_MATCH_DIALOG_SEEN);
    }

    @Override
    public void onStart() {
        super.onStart();

        matchDialogSeen = currentUser.getBoolean(ParseConstants.KEY_MATCH_DIALOG_SEEN);
        pickFriendsDialogSeen = currentUser.getBoolean(ParseConstants.KEY_PICK_FRIENDS_DIALOG_SEEN);
        active = true;

        if (currentUser.getBoolean(ParseConstants.KEY_IS_MATCHED) &&
                !matchDialogSeen) {
            try {
                mAdapter.showMatchDialog();
            } catch (JSONException | ParseException e) {
                showErrorDialog();
            }
        } else if (!currentUser.getBoolean(ParseConstants.KEY_IS_MATCHED) &&
                !pickFriendsDialogSeen) {
            mAdapter.showPickFriendsDialog();
        }
    }

    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_error_title)
                .setMessage(R.string.dialog_error_message)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog errorDialog = builder.create();
        errorDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.mainViewPager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    }

    private void setUpSlidingTabLayout() {
        mAdapter = new MainPagerAdapter(this, mSlidingTabLayout, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, R.id.tab_title);
        mSlidingTabLayout.setOnPageChangeListener(this);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String title;

        switch (position) {
            case 0:
                title = getString(R.string.settings_title);
                break;
            case 1:
                title = getString(R.string.this_weekend_title);
                break;
            case 2:
                title = getString(R.string.friends_title);
                break;
            default:
                title = getString(R.string.app_name);
                break;
        }
        setTitle(title);
        mAdapter.setCurrentPosition(position);

        EditText inputText = (EditText) findViewById(R.id.group_chat_message_input);
        if (inputText != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}