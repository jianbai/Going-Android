package io.spw.hello;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.firebase.client.Firebase;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;

// DONE: make chat pretty
// DONE: replace fragment transaction with a dialog
// TODO: HALF DONE fix lifecycle methods
// DONE: destroy some activities
// DONE: fix commit with state change THIS IS CRASHING
// DONE: use preferences api, get rid of sliders
// DONE: friends relations
// DONE: time :: keeping friends
// DONE: friend chat
// DONE: fix back button
// DONE: friend management / profile views
// TODO: location :: restrict to Vancouver
// TODO: push notifications
// TODO: icon badges
// TODO: remove ageSpread and genderSpread
// TODO: Refactor 1. Comments 2. Spacing 3. Variable names 4. Constant names
// TODO: Complete settings

public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    protected static final ParseUser currentUser = ParseUser.getCurrentUser();
    protected static final Firebase currentUserRef = new Firebase(FirebaseConstants.URL_USERS).child(currentUser.getObjectId());

    public static boolean active = false;
    public boolean matchDialogSeen;

    private SlidingTabLayout mSlidingTabLayout;
    public ViewPager mViewPager;
    private SectionsPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "CREATED");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        matchDialogSeen = currentUser.getBoolean(ParseConstants.KEY_MATCH_DIALOG_SEEN);

        findViews();
        setUpSlidingTabLayout();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.commit();
            Log.d(TAG, "Transaction COMMITTED");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "DESTROYED");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "PAUSED");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "RESTARTED");
        matchDialogSeen = currentUser.getBoolean(ParseConstants.KEY_MATCH_DIALOG_SEEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "RESUMED");
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
        Log.d(TAG, "STARTED");

        if (currentUser.getBoolean(ParseConstants.KEY_IS_MATCHED) &&
                !matchDialogSeen) {
            try {
                //showMatchDialog();
                mAdapter.showMatchDialog();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "STOPPED");
        active = false;
    }

    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    }

    private void setUpSlidingTabLayout() {
        mAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, R.id.tab_title);
        mSlidingTabLayout.setOnPageChangeListener(this);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    public void setViewPagerItem(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String title = getString(R.string.title_default);

        switch (position) {
            case 0:
                title = getString(R.string.title_section0);
                break;
            case 1:
                title = getString(R.string.title_section1);
                break;
            case 2:
                title = getString(R.string.title_section2);
                break;
            default:
                break;
        }
        setTitle(title);
        mAdapter.setCurrentPosition(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}