package io.spw.hello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by scottwang on 12/21/14.
 */
public class FriendsFragment extends ListFragment {

    public static final String TAG = FriendsFragment.class.getSimpleName();

    private Activity mainActivity;
    private ParseUser mCurrentUser;
    private ParseRelation<ParseUser> mFriendsRelation;
    private List<ParseUser> mFriends;
    private ListView mListView;

    public FriendsFragment(Activity c) {
        mainActivity = c;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "FRIENDS VIEW CREATED");
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
        mCurrentUser = MainActivity.currentUser;

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = getListView();

        setUpFriends();
    }

    private void setUpFriends() {
        if (mCurrentUser != null) {

            mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

            ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
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
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                }
            });
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String friendChatId;

        String currentUserObjectId = mCurrentUser.getObjectId();
        String friendObjectId = mFriends.get(position).getObjectId();
        double currentUserFacebookId =
                Double.parseDouble(mCurrentUser.getString(ParseConstants.KEY_FACEBOOK_ID));
        double friendFacebookId =
                Double.parseDouble(mFriends.get(position).getString(ParseConstants.KEY_FACEBOOK_ID));

        if (currentUserFacebookId < friendFacebookId) {
            friendChatId = currentUserObjectId + friendObjectId;
        } else {
            friendChatId = friendObjectId + currentUserObjectId;
        }

        navigateToChatActivity(friendChatId);
    }

    private void navigateToChatActivity(String friendChatId) {
        Intent intent = new Intent(mainActivity, FriendChatActivity.class);
        intent.putExtra("friendChatId", friendChatId);
        startActivity(intent);
    }
}
