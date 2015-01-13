package io.spw.hello;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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

    private ParseUser mCurrentUser;
    private ParseRelation<ParseUser> mFriendsRelation;
    private List<ParseUser> mFriends;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "FRIENDS VIEW CREATED");
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        mCurrentUser = MainActivity.currentUser;

        setUpFriends();

        return rootView;
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
                                getListView().getContext(),
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
}
