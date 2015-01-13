package io.spw.hello;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.firebase.client.ValueEventListener;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;

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
    private Firebase currentUserMatchedRef;
    protected ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;

    public ThisWeekendFragment(SectionsPagerAdapter.ThisWeekendFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_this_weekend, container, false);
        currentUser = MainActivity.currentUser;
        isSearching = currentUser.getBoolean(ParseConstants.KEY_IS_SEARCHING);
        Firebase currentUserRef = new Firebase(FirebaseConstants.URL_USERS).child(currentUser.getObjectId());
        currentUserMatchedRef = currentUserRef.child(FirebaseConstants.KEY_MATCHED);

        findViews(rootView);
        setUpEventListener();
        setUpViews();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
//        currentUserMatchedRef.addValueEventListener(valueEventListener);
//        currentUserMatchedRef.addChildEventListener(childEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(MainActivity.TAG, "STOPPED");
        currentUserMatchedRef.removeEventListener(valueEventListener);
        Log.d(MainActivity.TAG, "LISTENER REMOVED");
//        currentUserMatchedRef.removeEventListener(childEventListener);
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
            currentUserMatchedRef.addValueEventListener(valueEventListener);
            Log.d(MainActivity.TAG, "LISTENER ADDED");
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

                currentUserMatchedRef.addValueEventListener(valueEventListener);
                Log.d(MainActivity.TAG, "LISTENER ADDED");
//                currentUserMatchedRef.addChildEventListener(childEventListener);
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

    private void setUpEventListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(MainActivity.TAG, "FB TRIGGERED");
                Boolean isMatched;

                try {
                    isMatched = (Boolean) dataSnapshot.getValue();
                } catch (ClassCastException e) {
                    isMatched = false;
                }

                if (isMatched) {
                    Log.d(MainActivity.TAG, "MATCHED TRUE");
                    currentUserMatchedRef.removeEventListener(valueEventListener);
                    Log.d(MainActivity.TAG, "LISTENER REMOVED");
                    try {
                        listener.onMatchMade();
                    } catch (JSONException | ParseException e) {
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                } else {
                    Log.d(MainActivity.TAG, "MATCHED FALSE");
                }
//                if (isMatched) {
//                    try {
//                        listener.onMatchMade();
//                    } catch (JSONException e) {
//                        Log.d(TAG, e.getLocalizedMessage());
//                    } catch (ParseException e) {
//                        Log.d(TAG, e.getLocalizedMessage());
//                    }
//                    currentUser.put(ParseConstants.KEY_IS_SEARCHING, false);
//                    currentUser.put(ParseConstants.KEY_IS_MATCHED, true);
//                    currentUser.saveInBackground();
//                    currentUserMatchedRef.removeEventListener(valueEventListener);
//                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };

//        childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                try {
//                    listener.onMatchMade();
//                } catch (JSONException e) {
//                    Log.d(TAG, e.getLocalizedMessage());
//                } catch (ParseException e) {
//                    Log.d(TAG, e.getLocalizedMessage());
//                }
//                currentUser.put(ParseConstants.KEY_IS_SEARCHING, false);
//                currentUser.put(ParseConstants.KEY_IS_MATCHED, true);
//                currentUser.saveInBackground();
//                currentUserMatchedRef.removeEventListener(childEventListener);
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        };
    }

}