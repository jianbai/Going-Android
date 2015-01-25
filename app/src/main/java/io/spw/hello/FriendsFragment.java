/**
 * Created by @author scottwang on 12/21/14.
 */

package io.spw.hello;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Provides Friends page
 */
public class FriendsFragment extends ListFragment {

    private MainActivity mMainActivity;
    private SlidingTabLayout mSlidingTabLayout;
    private ListView mListView;
    private ParseUser mCurrentUser;
    private List<ParseUser> mFriends;

    /** Initializes member variables */
    public FriendsFragment(MainActivity activity, SlidingTabLayout slidingTabLayout) {
        mMainActivity = activity;
        mSlidingTabLayout = slidingTabLayout;
        mCurrentUser = mMainActivity.currentUser;
    }

    /** Sets up view in case of empty friends list */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        setUpButton(rootView);

        return rootView;
    }

    /** Gets and populates ListView */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = getListView();

        setUpFriends();
    }

    /** Handles click events for each cell in friends ListView */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String chatId;
        String friendName = mFriends.get(position).getString(ParseConstants.KEY_FIRST_NAME);
        String currentUserObjectId = mCurrentUser.getObjectId();
        String friendObjectId = mFriends.get(position).getObjectId();
        double currentUserFacebookId =
                Double.parseDouble(
                        mCurrentUser.getString(ParseConstants.KEY_FACEBOOK_ID));
        double friendFacebookId =
                Double.parseDouble(
                        mFriends.get(position).getString(ParseConstants.KEY_FACEBOOK_ID));

        if (currentUserFacebookId < friendFacebookId) {
            chatId = currentUserObjectId + friendObjectId;
        } else {
            chatId = friendObjectId + currentUserObjectId;
        }

        navigateToFriendChat(friendName, friendObjectId, chatId);
    }

    /** Sets up button for empty friends list */
    private void setUpButton(View rootView) {
        Button button = (Button) rootView.findViewById(R.id.friends_meet_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSlidingTabLayout.setTabPosition(1);
            }
        });
    }

    /** Fills ListView with friends from Parse */
    private void setUpFriends() {
        if (mCurrentUser != null) {
            ParseRelation<ParseUser> friendsRelation =
                    mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
            ParseQuery<ParseUser> query = friendsRelation.getQuery();
            query.orderByAscending(ParseConstants.KEY_FIRST_NAME);
            query.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> friends, ParseException e) {
                    if (e == null) {
                        mFriends = friends;

                        String[] names = new String[mFriends.size()];
                        for (int i=0; i<mFriends.size(); i++) {
                            names[i] = mFriends.get(i).getString(ParseConstants.KEY_FIRST_NAME);
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                mListView.getContext(),
                                android.R.layout.simple_list_item_1,
                                names
                        );
                        setListAdapter(adapter);
                    } else {
                        showFriendsErrorDialog();
                    }
                }
            });
        }
    }

    /** Shows error dialog */
    private void showFriendsErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle(R.string.dialog_error_title)
                .setMessage(R.string.friends_dialog_error_message)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog errorDialog = builder.create();
        errorDialog.show();
    }

    /** Navigates to FriendChatActivity */
    private void navigateToFriendChat(
            String friendName, String friendObjectId, String chatId) {
        Intent intent = new Intent(mMainActivity, FriendChatActivity.class);
        intent.putExtra("friendName", friendName);
        intent.putExtra("friendObjectId", friendObjectId);
        intent.putExtra("chatId", chatId);
        startActivity(intent);
    }

}
