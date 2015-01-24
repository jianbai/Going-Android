package io.spw.hello;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
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
public class MainPagerAdapter extends FragmentPagerAdapter {

    public static final String TAG = MainPagerAdapter.class.getSimpleName();

    private static Dialog mMatchDialog;
    private static AlertDialog mPickFriendsDialog;

    static final int NUM_ITEMS = 3;
    private Activity mActivity;
    private SlidingTabLayout mSlidingTabLayout;
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
    private int currentPosition;

    private ParseUser currentUser;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseRelation<ParseUser> mGroupMembersRelation;
    private List<ParseUser> mGroupMembers;

    private static boolean[] mFriendsToKeep = {
            false, false, false
    };

    public MainPagerAdapter(Activity a, SlidingTabLayout s, FragmentManager fm) {
        super(fm);
        mActivity = a;
        mSlidingTabLayout = s;
        mFragmentManager = fm;
        currentUser = MainActivity.currentUser;
        mFriendsRelation =
                currentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        mGroupMembersRelation =
                currentUser.getRelation(ParseConstants.KEY_GROUP_MEMBERS_RELATION);
        currentPosition = 1;
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
        boolean isMatched = currentUser.getBoolean(ParseConstants.KEY_IS_MATCHED);

        switch (position) {
            case 0:
                return new SettingsFragment(mActivity);
            case 1:
                if (isMatched) {
                    mFirstFragment = new GroupChatFragment(mActivity, new GroupChatFragmentListener() {
                        @Override
                        public void onFriendsPicked() {
                            currentUser.put(ParseConstants.KEY_IS_MATCHED, false);
                            currentUser.saveInBackground();

                            showPickFriendsDialog();
                        }
                    });
                } else {
                    mFirstFragment = new ThisWeekendFragment(mActivity,
                            new ThisWeekendFragmentListener() {
                        @Override
                        public void onMatchMade() throws JSONException, ParseException {
                            currentUser.put(ParseConstants.KEY_IS_SEARCHING, false);
                            currentUser.put(ParseConstants.KEY_IS_MATCHED, true);
                            currentUser.saveInBackground();

                            showMatchDialog();
                        }
                    });
                }
                return mFirstFragment;
            case 2:
                if (mFriendsFragment == null) {
                    mFriendsFragment = new FriendsFragment(mActivity, mSlidingTabLayout);
                }
                return mFriendsFragment;
            default:
                return null;
        }
    }

    public void showPickFriendsDialog() {
        if (mPickFriendsDialog == null || !mPickFriendsDialog.isShowing()) {
            ParseQuery<ParseUser> query = mGroupMembersRelation.getQuery();
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> parseUsers, ParseException e) {
                    if (e == null) {
                        mGroupMembers = parseUsers;

                        String[] names = new String[mGroupMembers.size()];
                        for (int i = 0; i < mGroupMembers.size(); i++) {
                            if (!mGroupMembers.get(i).getObjectId()
                                    .equals(currentUser.getObjectId())) {
                                names[i] = mGroupMembers.get(i).getString(ParseConstants.KEY_FIRST_NAME);
                            }
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle(mActivity.getString(R.string.dialog_pick_friends_title))
                                .setMultiChoiceItems(names, mFriendsToKeep,
                                        new DialogInterface.OnMultiChoiceClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which,
                                                                boolean isChecked) {
                                                mFriendsToKeep[which] = isChecked;
                                            }
                                        })
                                .setPositiveButton(mActivity.getString(R.string.dialog_pick_friends_button),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                for (int i = 0; i < mGroupMembers.size(); i++) {
                                                    mGroupMembersRelation.remove(mGroupMembers.get(i));
                                                    if (mFriendsToKeep[i]) {
                                                        mFriendsRelation.add(mGroupMembers.get(i));
                                                    }
                                                }
                                                ParseInstallation.getCurrentInstallation()
                                                        .remove(ParseConstants.KEY_INSTALLATION_GROUP_ID);
                                                ParseInstallation.getCurrentInstallation().saveInBackground();

                                                currentUser.put(ParseConstants.KEY_PICK_FRIENDS_DIALOG_SEEN, true);
                                                currentUser.put(ParseConstants.KEY_MATCH_DIALOG_SEEN, false);
                                                currentUser.saveInBackground();

                                                if (mFirstFragment instanceof GroupChatFragment) {
                                                    switchToThisWeekendFragment();
                                                    updateFriendsFragment();
                                                }
                                            }
                                        });

                        mPickFriendsDialog = builder.create();
                        mPickFriendsDialog.show();
                    }
                }
            });
        }
    }

    public void showMatchDialog() throws JSONException, ParseException {
        if (mMatchDialog == null || !mMatchDialog.isShowing()) {
            mMatchDialog = new Dialog(mActivity);
            mMatchDialog.setContentView(R.layout.dialog_match_made);
            mMatchDialog.setTitle(R.string.dialog_match_made_title);

            // find views
            TextView userName0 = (TextView) mMatchDialog.findViewById(R.id.userName0);
            TextView userInfo0 = (TextView) mMatchDialog.findViewById(R.id.userInfo0);
            TextView userName1 = (TextView) mMatchDialog.findViewById(R.id.userName1);
            TextView userInfo1 = (TextView) mMatchDialog.findViewById(R.id.userInfo1);
            TextView userName2 = (TextView) mMatchDialog.findViewById(R.id.userName2);
            TextView userInfo2 = (TextView) mMatchDialog.findViewById(R.id.userInfo2);
            Button helloButton = (Button) mMatchDialog.findViewById(R.id.dialog_hello_button);

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
                    "  : :  " +
                    (users.get(0).getString(ParseConstants.KEY_HOMETOWN)));

            userName1.setText(users.get(1).getString(ParseConstants.KEY_FIRST_NAME));
            userInfo1.setText(users.get(1).getString(ParseConstants.KEY_AGE) +
                    "  : :  " +
                    (users.get(1).getString(ParseConstants.KEY_HOMETOWN)));

            userName2.setText(users.get(2).getString(ParseConstants.KEY_FIRST_NAME));
            userInfo2.setText(users.get(2).getString(ParseConstants.KEY_AGE) +
                    "  : :  " +
                    (users.get(2).getString(ParseConstants.KEY_HOMETOWN)));

            // set button
            helloButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFirstFragment instanceof ThisWeekendFragment) {
                        switchToGroupChatFragment();
                    }

                    ParseInstallation.getCurrentInstallation().
                            put(ParseConstants.KEY_INSTALLATION_GROUP_ID,
                                    currentUser.getString(ParseConstants.KEY_GROUP_ID));
                    ParseInstallation.getCurrentInstallation().saveInBackground();

                    currentUser.put(ParseConstants.KEY_PICK_FRIENDS_DIALOG_SEEN, false);
                    currentUser.put(ParseConstants.KEY_MATCH_DIALOG_SEEN, true);
                    currentUser.saveInBackground();

                    mMatchDialog.dismiss();
                }
            });

            mMatchDialog.show();
        }
    }

    public void updateFriendsFragment() {
        mFragmentManager.beginTransaction().remove(mFriendsFragment).commit();
        mFriendsFragment = new FriendsFragment(mActivity, mSlidingTabLayout);
        notifyDataSetChanged();
    }

    public void switchToGroupChatFragment() {
        mFragmentManager.beginTransaction().remove(mFirstFragment).commit();
        mFirstFragment = new GroupChatFragment(mActivity, new GroupChatFragmentListener() {
            @Override
            public void onFriendsPicked() {
                currentUser.put(ParseConstants.KEY_IS_MATCHED, false);
                currentUser.saveInBackground();

                showPickFriendsDialog();
            }
        });
        notifyDataSetChanged();
    }

    public void switchToThisWeekendFragment() {
        mFragmentManager.beginTransaction().remove(mFirstFragment).commit();
        mFirstFragment = new ThisWeekendFragment(mActivity,
                new ThisWeekendFragmentListener() {
            @Override
            public void onMatchMade() throws JSONException, ParseException {
                currentUser.put(ParseConstants.KEY_IS_SEARCHING, false);
                currentUser.put(ParseConstants.KEY_IS_MATCHED, true);
                currentUser.saveInBackground();

                showMatchDialog();
            }
        });
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int position) {
        currentPosition = position;
    }

    public interface ThisWeekendFragmentListener {
        void onMatchMade() throws JSONException, ParseException;
    }

    public interface GroupChatFragmentListener {
        void onFriendsPicked();
    }

}
