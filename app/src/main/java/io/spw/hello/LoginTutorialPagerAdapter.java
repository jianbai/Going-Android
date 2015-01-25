/**
 * Created by scott on 1/22/15.
 */

package io.spw.hello;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/** Provides custom FragmentPagerAdapter implementation for login tutorial */
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
                return new LoginFragment0();
            case 1:
                return new LoginFragment1();
            case 2:
                return new LoginFragment2();
            case 3:
                return new LoginFragment3();
            default:
                return null;
        }
    }

}
