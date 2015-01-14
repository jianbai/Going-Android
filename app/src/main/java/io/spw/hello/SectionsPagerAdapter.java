package io.spw.hello;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scottwang on 12/21/14.
 * Settings and Friends icons, made by Freepik from www.flaticon.com, is licensed under CC BY 3.0
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public static final String TAG = SectionsPagerAdapter.class.getSimpleName();

    static final int NUM_ITEMS = 3;
    private Activity mActivity;
    private final FragmentManager mFragmentManager;
    protected Fragment mFriendsFragment;
    protected Fragment mFirstFragment;
    public static int[] imageResIdUnselected = {
            R.drawable.ic_settings_icon_unselected,
            R.drawable.ic_this_weekend_icon_unselected,
            R.drawable.ic_friends_icon_unselected
    };

    public static int[] imageResIdSelected = {
            R.drawable.ic_settings_icon,
            R.drawable.ic_this_weekend_icon,
            R.drawable.ic_friends_icon
    };
//    private boolean isMatched;

    private ParseUser currentUser;

    public SectionsPagerAdapter(Activity a, FragmentManager fm) {
        super(fm);
        mActivity = a;
        mFragmentManager = fm;
        currentUser = MainActivity.currentUser;
        // isMatched = currentUser.getBoolean(ParseConstants.KEY_IS_MATCHED);

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
        } else if (object instanceof GroupChatFragment &&
                mFirstFragment instanceof ThisWeekendFragment) {
            return POSITION_NONE;
        } else if (object instanceof FriendsFragment) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = mActivity.getResources().getDrawable(imageResIdUnselected[position]);

        int currentPosition = ((MainActivity) mActivity).mViewPager.getCurrentItem();

        if (position == currentPosition) {
            image = mActivity.getResources().getDrawable(imageResIdSelected[position]);
        }

        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicWidth());
        SpannableString ss = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        ss.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;

    }

    @Override
    public Fragment getItem(int position) {
        Log.d(MainActivity.TAG, "GET ITEM");
        boolean isMatched = currentUser.getBoolean(ParseConstants.KEY_IS_MATCHED);

        switch (position) {
            case 0:
                return new SettingsFragment(mActivity);
            case 1:
                if (isMatched) {
                    mFirstFragment = new GroupChatFragment(mActivity, new GroupChatFragmentListener() {
                        @Override
                        public void onFriendsPicked() {
                            switchToThisWeekendFragment();
                            updateFriendsFragment();
                        }
                    });
                } else {
                    mFirstFragment = new ThisWeekendFragment(new ThisWeekendFragmentListener() {
                        @Override
                        public void onMatchMade() throws JSONException, ParseException {
                            currentUser.put(ParseConstants.KEY_IS_SEARCHING, false);
                            currentUser.put(ParseConstants.KEY_IS_MATCHED, true);
                            currentUser.saveInBackground();
                            if (MainActivity.active) {
                                showMatchDialog();
                            }
                        }
                    });
                }
                return mFirstFragment;
            case 2:
                if (mFriendsFragment == null) {
                    mFriendsFragment = new FriendsFragment(mActivity);
                }
                return mFriendsFragment;
            default:
                return null;
        }
    }

    public void showMatchDialog() throws JSONException, ParseException {

        final Dialog dialog = new Dialog(mActivity);
        dialog.setContentView(R.layout.dialog_match_made);
        dialog.setTitle("An introduction is the essence of possibility");

        // find views
        TextView userName0 = (TextView) dialog.findViewById(R.id.userName0);
        TextView userInfo0 = (TextView) dialog.findViewById(R.id.userInfo0);
        TextView userName1 = (TextView) dialog.findViewById(R.id.userName1);
        TextView userInfo1 = (TextView) dialog.findViewById(R.id.userInfo1);
        TextView userName2 = (TextView) dialog.findViewById(R.id.userName2);
        TextView userInfo2 = (TextView) dialog.findViewById(R.id.userInfo2);
        Button helloButton = (Button) dialog.findViewById(R.id.dialog_hello_button);

        // get member users
        JSONArray userIds = currentUser.getJSONArray(ParseConstants.KEY_MEMBER_IDS);
        ParseRelation<ParseUser> groupMembersRelation = currentUser.getRelation(ParseConstants.KEY_GROUP_MEMBERS_RELATION);
        List<ParseUser> users = new ArrayList<>();
        for (int i=0; i<userIds.length(); i++) {
            if (!currentUser.getObjectId().equals(userIds.getString(i))) {
                ParseUser user = ParseUser.getQuery().get(userIds.getString(i));
                users.add(user);
                groupMembersRelation.add(user);
            }
        }

        // set text
        userName0.setText(users.get(0).getString(ParseConstants.KEY_FIRST_NAME));
        userInfo0.setText(users.get(0).getString(ParseConstants.KEY_AGE) +
                " // " +
                (users.get(0).getString(ParseConstants.KEY_HOMETOWN)));

        userName1.setText(users.get(1).getString(ParseConstants.KEY_FIRST_NAME));
        userInfo1.setText(users.get(1).getString(ParseConstants.KEY_AGE) +
                " // " +
                (users.get(1).getString(ParseConstants.KEY_HOMETOWN)));

        userName2.setText(users.get(2).getString(ParseConstants.KEY_FIRST_NAME));
        userInfo2.setText(users.get(2).getString(ParseConstants.KEY_AGE) +
                " // " +
                (users.get(2).getString(ParseConstants.KEY_HOMETOWN)));

        // set button
        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.put(ParseConstants.KEY_MATCH_DIALOG_SEEN, true);
                currentUser.saveInBackground();
                if (mFirstFragment instanceof ThisWeekendFragment) {
                    switchToGroupChatFragment();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void updateFriendsFragment() {
        mFragmentManager.beginTransaction().remove(mFriendsFragment).commit();
        mFriendsFragment = new FriendsFragment(mActivity);
        notifyDataSetChanged();
    }

    public void switchToGroupChatFragment() {
        mFragmentManager.beginTransaction().remove(mFirstFragment).commit();
        mFirstFragment = new GroupChatFragment(mActivity, new GroupChatFragmentListener() {
            @Override
            public void onFriendsPicked() {
                switchToThisWeekendFragment();
            }
        });
        notifyDataSetChanged();
    }

    public void switchToThisWeekendFragment() {
        mFragmentManager.beginTransaction().remove(mFirstFragment).commit();
        mFirstFragment = new ThisWeekendFragment(new ThisWeekendFragmentListener() {
            @Override
            public void onMatchMade() throws JSONException, ParseException {
                currentUser.put(ParseConstants.KEY_IS_SEARCHING, false);
                currentUser.put(ParseConstants.KEY_IS_MATCHED, true);
                currentUser.saveInBackground();
                if (MainActivity.active) {
                    showMatchDialog();
                }
            }
        });
        notifyDataSetChanged();
    }

    public interface ThisWeekendFragmentListener {
        void onMatchMade() throws JSONException, ParseException;
    }

    public interface GroupChatFragmentListener {
        void onFriendsPicked();
    }

}
