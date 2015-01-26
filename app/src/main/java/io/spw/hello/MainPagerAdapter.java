/**
 * Created by @author scottwang on 12/21/14.
 * Settings and Friends icons, made by Freepik from www.flaticon.com, are licensed under CC BY 3.0
 */

package io.spw.hello;

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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Provides custom implementation of FragmentPagerAdapter for main ViewPager
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    public static final int NUM_ITEMS = 3;
    private static Dialog sMatchMadeDialog;
    private static AlertDialog sMatchExpiredDialog;
    private MainActivity mMainActivity;
    private SlidingTabLayout mSlidingTabLayout;
    private FragmentManager mFragmentManager;
    private Fragment mCenterFragment;
    private Fragment mFriendsFragment;
    private ParseUser mCurrentUser;
    private ParseInstallation mInstallation;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseRelation<ParseUser> mGroupMembersRelation;
    private List<ParseUser> mGroupMembers;
    private String[] mNames;
    private int mCurrentPosition;
    private int[] mImageResIdsUnselected = {
            R.drawable.ic_settings_icon_unselected,
            R.drawable.ic_this_weekend_icon_unselected,
            R.drawable.ic_friends_icon_unselected
    };
    private int[] mImageResIdsSelected = {
            R.drawable.ic_settings_icon,
            R.drawable.ic_this_weekend_icon,
            R.drawable.ic_friends_icon
    };
    private boolean[] mFriendsToKeep = {
            false, false, false
    };

    /** Initializes member variables */
    public MainPagerAdapter(MainActivity a, SlidingTabLayout s, FragmentManager fm) {
        super(fm);
        mMainActivity = a;
        mSlidingTabLayout = s;
        mFragmentManager = fm;
        mCurrentUser = ParseUser.getCurrentUser();
        mInstallation = ParseInstallation.getCurrentInstallation();
        mFriendsRelation =
                mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        mGroupMembersRelation =
                mCurrentUser.getRelation(ParseConstants.KEY_GROUP_MEMBERS_RELATION);
        mCurrentPosition = 1;
    }

    /** Returns total number of fragments */
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    /** Overrides default value of POSITION_UNCHANGED for fragments that are dynamically changed */
    @Override
    public int getItemPosition(Object object) {
        if (object instanceof ThisWeekendFragment &&
                mCenterFragment instanceof GroupChatFragment) {
            return POSITION_NONE;
        } else if (object instanceof GroupChatFragment &&
                mCenterFragment instanceof ThisWeekendFragment) {
            return POSITION_NONE;
        } else if (object instanceof FriendsFragment) {
            return POSITION_NONE;
        } else {
            return POSITION_UNCHANGED;
        }

    }

    /** Fills action bar with icons */
    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = mMainActivity.getResources().getDrawable(mImageResIdsUnselected[position]);

        if (position == mCurrentPosition) {
            image = mMainActivity.getResources().getDrawable(mImageResIdsSelected[position]);
        }

        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicWidth());
        SpannableString ss = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        ss.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;

    }

    /** Returns appropriate fragment based on which page is currently selected */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            // Settings
            case 0:
                return new SettingsFragment(mMainActivity);
            // This Weekend or Group Chat
            case 1:
                // Check is user is matched on Parse and return appropriate fragment
                if (mCurrentUser.getBoolean(ParseConstants.KEY_IS_MATCHED)) {
                    mCenterFragment = new GroupChatFragment(mMainActivity, new GroupChatFragmentListener() {
                        @Override
                        public void onMatchExpired() {
                            handleMatchExpired();
                        }
                    });
                } else {
                    mCenterFragment = new ThisWeekendFragment(mMainActivity,
                            new ThisWeekendFragmentListener() {
                        @Override
                        public void onMatchMade() {
                            handleMatchMade();
                        }
                    });
                }
                return mCenterFragment;
            // Friends
            case 2:
                if (mFriendsFragment == null) {
                    mFriendsFragment = new FriendsFragment(mMainActivity, mSlidingTabLayout);
                }
                return mFriendsFragment;
            default:
                return null;
        }
    }

    /** Updates Parse values and shows match dialog when match is made */
    private void handleMatchMade() {
        mCurrentUser.put(ParseConstants.KEY_IS_SEARCHING, false);
        mCurrentUser.put(ParseConstants.KEY_IS_MATCHED, true);
        mCurrentUser.saveInBackground();

        showMatchMadeDialog();
    }

    /** Updates Parse values and shows pick friends dialog when match expires */
    private void handleMatchExpired() {
        mCurrentUser.put(ParseConstants.KEY_IS_MATCHED, false);
        mCurrentUser.saveInBackground();

        showMatchExpiredDialog();
    }

    /** Shows dialog displaying matches to user */
    public void showMatchMadeDialog() {
        if (sMatchMadeDialog == null || !sMatchMadeDialog.isShowing()) {
            sMatchMadeDialog = new MatchMadeDialog(mMainActivity, new MatchMadeDialogListener() {
                @Override
                public void onMatchMadeDialogSeen() {
                    handleMatchMadeDialogSeen();
                }
            });

            sMatchMadeDialog.show();
        }
    }

    /** Shows dialog for picking friends to add */
    public void showMatchExpiredDialog() {
        if (sMatchExpiredDialog == null || !sMatchExpiredDialog.isShowing()) {
            ParseQuery<ParseUser> query = mGroupMembersRelation.getQuery();
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> groupMembers, ParseException e) {
                    if (e == null) {
                        updateGroupMembers(groupMembers);

                        sMatchExpiredDialog = createMatchExpiredDialog();
                        sMatchExpiredDialog.show();
                    }
                }
            });
        }
    }

    /** Updates group members and their names from Parse */
    private void updateGroupMembers(List<ParseUser> groupMembers) {
        mGroupMembers = groupMembers;
        mNames = new String[groupMembers.size()];
        for (int i = 0; i < groupMembers.size(); i++) {
            if (!groupMembers.get(i).getObjectId()
                    .equals(mCurrentUser.getObjectId())) {
                mNames[i] = groupMembers.get(i).getString(ParseConstants.KEY_FIRST_NAME);
            }
        }
    }

    /** Returns an AlertDialog for picking friends */
    private AlertDialog createMatchExpiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle(mMainActivity.getString(R.string.group_chat_dialog_pick_friends_title))
                .setMultiChoiceItems(mNames, mFriendsToKeep,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                // Update list of friends to keep
                                mFriendsToKeep[which] = isChecked;
                            }
                        })
                .setPositiveButton(mMainActivity.getString(R.string.group_chat_dialog_pick_friends_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                handleMatchExpiredDialogSeen();
                            }
                        });

        return builder.create();
    }

    /** Updates Parse data and switches center fragment */
    private void handleMatchMadeDialogSeen() {
        mCurrentUser.put(ParseConstants.KEY_PICK_FRIENDS_DIALOG_SEEN, false);
        mCurrentUser.put(ParseConstants.KEY_MATCH_DIALOG_SEEN, true);
        mCurrentUser.saveInBackground();

        if (mInstallation != null) {
            mInstallation.put(ParseConstants.KEY_INSTALLATION_GROUP_ID,
                    mCurrentUser.getString(ParseConstants.KEY_GROUP_ID));
            mInstallation.saveInBackground();
        }

        if (mCenterFragment instanceof ThisWeekendFragment) {
            switchToGroupChatFragment();
        }
    }

    /** Updates Parse data and switches center fragment */
    private void handleMatchExpiredDialogSeen() {
        // Update ParseRelations
        for (int i = 0; i < mGroupMembers.size(); i++) {
            mGroupMembersRelation.remove(mGroupMembers.get(i));
            if (mFriendsToKeep[i]) {
                mFriendsRelation.add(mGroupMembers.get(i));
            }
            mFriendsToKeep[i] = false;
        }

        mGroupMembers = null;
        // Update ParseUser and save to Parse
        mCurrentUser.put(ParseConstants.KEY_PICK_FRIENDS_DIALOG_SEEN, true);
        mCurrentUser.put(ParseConstants.KEY_MATCH_DIALOG_SEEN, false);
        mCurrentUser.saveInBackground();

        // Update ParseInstallation and save to Parse
        if (mInstallation != null) {
            mInstallation.remove(ParseConstants.KEY_INSTALLATION_GROUP_ID);
            mInstallation.saveInBackground();
        }

        // Switch center fragment and update friends
        if (mCenterFragment instanceof GroupChatFragment) {
            updateFriendsFragment();
            switchToThisWeekendFragment();
        }
    }

    /** Reloads FriendsFragment */
    public void updateFriendsFragment() {
        mFragmentManager.beginTransaction().remove(mFriendsFragment).commit();
        mFriendsFragment = new FriendsFragment(mMainActivity, mSlidingTabLayout);
        notifyDataSetChanged();
    }

    /** Switches center fragment to GroupChatFragment */
    public void switchToGroupChatFragment() {
        mFragmentManager.beginTransaction().remove(mCenterFragment).commit();
        mCenterFragment = new GroupChatFragment(mMainActivity, new GroupChatFragmentListener() {
            @Override
            public void onMatchExpired() {
                handleMatchExpired();
            }
        });
        notifyDataSetChanged();
    }

    /** Switches center fragment to ThisWeekendFragment */
    public void switchToThisWeekendFragment() {
        mFragmentManager.beginTransaction().remove(mCenterFragment).commit();
        mCenterFragment = new ThisWeekendFragment(mMainActivity,
                new ThisWeekendFragmentListener() {
            @Override
            public void onMatchMade() {
                handleMatchMade();
            }
        });
        notifyDataSetChanged();
    }

    /** Sets current position */
    public void setCurrentPosition(int position) {
        mCurrentPosition = position;
    }

    /** Provides interface for ThisWeekendFragment listener */
    public interface ThisWeekendFragmentListener {
        void onMatchMade();
    }

    /** Provides interface for GroupChatFragment listener */
    public interface GroupChatFragmentListener {
        void onMatchExpired();
    }

    /** Provides interface for MatchMadeDialog listener */
    public interface MatchMadeDialogListener {
        void onMatchMadeDialogSeen();
    }

}