package io.spw.hello;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.parse.ParseUser;

// DONE: make chat pretty
// DONE: replace fragment transaction with a dialog
// TODO: fix lifecycle methods
// DONE: destroy some activities
// TODO: fix commit with state change THIS IS CRASHING
// DONE: use preferences api, get rid of sliders
// TODO: friends relations
// TODO: time :: keeping friends
// TODO: location :: restrict to Vancouver
// TODO: push notifications

public class MainActivity extends ActionBarActivity implements ViewPager.OnPageChangeListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    protected static final ParseUser currentUser = ParseUser.getCurrentUser();

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        setUpSlidingTabLayout();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.commit();
        }
    }

    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
    }

    private void setUpSlidingTabLayout() {
        mViewPager.setAdapter(new SectionsPagerAdapter(this, getSupportFragmentManager()));
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
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}