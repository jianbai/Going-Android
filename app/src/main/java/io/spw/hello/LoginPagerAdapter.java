package io.spw.hello;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by scott on 1/22/15.
 */
public class LoginPagerAdapter extends FragmentPagerAdapter {

    public static final String TAG = LoginPagerAdapter.class.getSimpleName();

    static final int NUM_ITEMS = 4;

    private final FragmentManager mFragmentManager;

    public LoginPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            // Go
            case 0:
                return new LoginFragment0();
            // Go out
            case 1:
                return new LoginFragment1();
            // Go again
            case 2:
                return new LoginFragment2();
            // Privacy
            case 3:
                return new LoginFragment3();
            default:
                return null;
        }

    }
}
