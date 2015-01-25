/**
 * Created by @author scott on 1/22/15.
 */

package io.spw.hello;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Provides custom FragmentPagerAdapter implementation for login tutorial
 */
public class LoginTutorialPagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_ITEMS = 4;

    /** Provides required constructor */
    public LoginTutorialPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    /** Sets number of fragments */
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    /** Returns appropriate fragment based on current position */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LoginTutorialFragment(R.layout.fragment_login_0);
            case 1:
                return new LoginTutorialFragment(R.layout.fragment_login_1);
            case 2:
                return new LoginTutorialFragment(R.layout.fragment_login_2);
            case 3:
                return new LoginTutorialFragment(R.layout.fragment_login_3);
            default:
                return null;
        }
    }

}
