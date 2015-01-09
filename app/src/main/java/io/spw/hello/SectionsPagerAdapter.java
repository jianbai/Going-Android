package io.spw.hello;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.parse.ParseUser;

import java.util.Locale;

/**
 * Created by scottwang on 12/21/14.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    static final int NUM_ITEMS = 2;
    private Context mContext;
    private final FragmentManager mFragmentManager;
    private static Fragment mFirstFragment;
    private Boolean isMatched;

    public SectionsPagerAdapter(Context c, FragmentManager fm) {
        super(fm);
        mContext = c;
        mFragmentManager = fm;
        isMatched = ParseUser.getCurrentUser().getBoolean(ParseConstants.KEY_IS_MATCHED);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof ThisWeekendFragment &&
                mFirstFragment instanceof GroupChatFragment) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();

        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
        }

        return null;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (mFirstFragment == null) {
                    if (isMatched) {
                        mFirstFragment = new GroupChatFragment();
                    } else {
                        mFirstFragment = new ThisWeekendFragment(new ThisWeekendFragmentListener() {
                            @Override
                            public void onSwitchToGroupChat() {
                                mFragmentManager.beginTransaction().remove(mFirstFragment).commitAllowingStateLoss();
                                mFirstFragment = new GroupChatFragment();
                                notifyDataSetChanged();
                            }
                        });
                    }
                }
                return mFirstFragment;
            case 1:
                return new FriendsFragment();
            default:
                return null;
        }
    }

    public interface ThisWeekendFragmentListener {
        void onSwitchToGroupChat();
    }

}
