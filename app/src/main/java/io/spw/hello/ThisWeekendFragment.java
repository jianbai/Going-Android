package io.spw.hello;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.parse.ParseUser;

/**
 * Created by scottwang on 12/20/14.
 */
public class ThisWeekendFragment extends Fragment {

    public static final String TAG = ThisWeekendFragment.class.getSimpleName();

    private SectionsPagerAdapter.ThisWeekendFragmentListener listener;

    private TextView mFreeTextView;
    private TextView mBullet1TextView;
    private TextView mBullet2TextView;
    private TextView mBullet3TextView;
    private Button mHelloButton;
    private TextView mSearchingTextView;
    private ProgressBar mProgressSpinner;

    private ParseUser currentUser;
    private Boolean isSearching;
    private Firebase currentUserRef;
    private ChildEventListener childEventListener;

    public ThisWeekendFragment(SectionsPagerAdapter.ThisWeekendFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_this_weekend, container, false);
        currentUser = ParseUser.getCurrentUser();
        isSearching = currentUser.getBoolean(ParseConstants.KEY_IS_SEARCHING);
        Firebase usersRef = new Firebase(FirebaseConstants.URL_USERS);
        currentUserRef = usersRef.child(currentUser.getObjectId());

        findViews(rootView);
        setUpViews();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpViews();
    }

    private void findViews(View rootView) {
        mFreeTextView = (TextView) rootView.findViewById(R.id.this_weekend_free_textview);
        mBullet1TextView = (TextView) rootView.findViewById(R.id.this_weekend_bullet_1_textview);
        mBullet2TextView = (TextView) rootView.findViewById(R.id.this_weekend_bullet_2_textview);
        mBullet3TextView = (TextView) rootView.findViewById(R.id.this_weekend_bullet_3_textview);
        mHelloButton = (Button) rootView.findViewById(R.id.this_weekend_hello_button);
        mSearchingTextView = (TextView) rootView.findViewById(R.id.this_weekend_searching_textview);
        mProgressSpinner = (ProgressBar) rootView.findViewById(R.id.this_weekend_progress_spinner);
    }

    private void setUpViews() {
        if (isSearching) {
            showProgressSpinner();
        } else {
            hideProgressSpinner();
            setUpHelloButton();
        }
    }

    private void setUpHelloButton() {
        mHelloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.put(ParseConstants.KEY_IS_SEARCHING, true);
                currentUser.saveInBackground();

                showProgressSpinner();

                childEventListener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        listener.onSwitchToGroupChat();
                        currentUser.put(ParseConstants.KEY_IS_SEARCHING, false);
                        currentUser.put(ParseConstants.KEY_IS_MATCHED, true);
                        currentUser.saveInBackground();
                        currentUserRef.removeEventListener(childEventListener);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                };

                currentUserRef.addChildEventListener(childEventListener);
            }
        });
    }

    private void showProgressSpinner() {
        mFreeTextView.setVisibility(View.GONE);
        mBullet1TextView.setVisibility(View.GONE);
        mBullet2TextView.setVisibility(View.GONE);
        mBullet3TextView.setVisibility(View.GONE);
        mHelloButton.setVisibility(View.GONE);
        mSearchingTextView.setVisibility(View.VISIBLE);
        mProgressSpinner.setVisibility(View.VISIBLE);
    }

    private void hideProgressSpinner() {
        mFreeTextView.setVisibility(View.VISIBLE);
        mBullet1TextView.setVisibility(View.VISIBLE);
        mBullet2TextView.setVisibility(View.VISIBLE);
        mBullet3TextView.setVisibility(View.VISIBLE);
        mHelloButton.setVisibility(View.VISIBLE);
        mSearchingTextView.setVisibility(View.GONE);
        mProgressSpinner.setVisibility(View.GONE);
    }

}