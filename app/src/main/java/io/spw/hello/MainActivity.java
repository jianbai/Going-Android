/**
 * Created by @author scottwang on 12/20/14.
 */

package io.spw.hello;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.firebase.client.Firebase;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

/**
 * Provides main activity containing main ViewPager with 3 pages
 * for SettingsFragment, ThisWeekendFragment and FriendFragment
 */
public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    public Firebase currentUserRef;
    private ParseUser currentUser;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private MainPagerAdapter mAdapter;

    /** Saves device info to Parse and sets up sliding tabs */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();

        // Initialize ParseUser and Firebase variables
        currentUser = ParseUser.getCurrentUser();
        currentUserRef =
                new Firebase(FirebaseConstants.URL_USERS).child(currentUser.getObjectId());

        // Save device ParseInstallation info
        saveParseInstallation();

        // Set up SlidingTabLayout and commit transaction
        setUpSlidingTabLayout();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().commit();
        }
    }

    /** Checks Parse to see if any dialogs should be shown */
    @Override
    public void onResume() {
        super.onResume();

        Boolean matchDialogSeen =
                currentUser.getBoolean(ParseConstants.KEY_MATCH_DIALOG_SEEN);
        Boolean pickFriendsDialogSeen =
                currentUser.getBoolean(ParseConstants.KEY_PICK_FRIENDS_DIALOG_SEEN);

        if (currentUser.getBoolean(ParseConstants.KEY_IS_MATCHED) &&
                !matchDialogSeen) {
            // If user is matched, but has not seen their matches, show the match dialog
            mAdapter.showMatchMadeDialog();
        } else if (!currentUser.getBoolean(ParseConstants.KEY_IS_MATCHED) &&
                !pickFriendsDialogSeen) {
            // If user is not matched, but has not picked friends, show the pick friend dialog
            mAdapter.showMatchExpiredDialog();
        }
    }

    /** Satisfies required method implementation for OnPageChangeListener */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    /** Updates title and hides keyboard when page is changed */
    @Override
    public void onPageSelected(int position) {
        // Update title based on position
        updateTitle(position);
        // Update MainPagerAdapter's currentPosition
        mAdapter.setCurrentPosition(position);
        // Hide keyboard on page change
        hideKeyboard();
    }

    /** Satisfies required method implementation for OnPageChangeListener */
    @Override
    public void onPageScrollStateChanged(int state) {}

    /** Finds views */
    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    }

    /** Sets up fragments and sliding tabs */
    private void setUpSlidingTabLayout() {
        mAdapter = new MainPagerAdapter(this, mSlidingTabLayout, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, R.id.tab_title);
        mSlidingTabLayout.setOnPageChangeListener(this);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    /** Saves device info to Parse */
    private void saveParseInstallation() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        if (installation != null) {
            installation.put(ParseConstants.KEY_INSTALLATION_USER_ID,
                    currentUser.getObjectId());
            installation.put(ParseConstants.KEY_INSTALLATION_USER_NAME,
                    currentUser.getString(ParseConstants.KEY_FIRST_NAME));
            installation.saveInBackground();
        }
    }

    /** Updates title in action bar based on which page is selected */
    private void updateTitle(int position) {
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
    }

    /** Hides keyboard */
    private void hideKeyboard() {
        EditText inputText = (EditText) findViewById(R.id.group_chat_message_input);
        if (inputText != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            inputMethodManager.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
        }
    }

}