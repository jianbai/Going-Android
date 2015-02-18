/**
 * Created by @author scottwang on 12/20/14.
 */

package xyz.getgoing.going;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.parse.ParseUser;

/**
 * Provides This Weekend page
 */
public class ThisWeekendFragment extends Fragment {

    private MainActivity mMainActivity;
    private MainPagerAdapter.ThisWeekendFragmentListener mListener;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private LinearLayout mLinearLayout;
    private Button mGoButton;
    private TextView mHelpTextView;
    private LinearLayout mSpinnerLayout;
    private ParseUser mCurrentUser;
    private Boolean mIsSearching;
    private Firebase mCurrentUserMatchedRef;
    private ValueEventListener mValueEventListener;

    /** Initializes member variables */
    public ThisWeekendFragment(MainActivity activity,
                               MainPagerAdapter.ThisWeekendFragmentListener listener) {
        mMainActivity = activity;
        mListener = listener;
        mCurrentUser = ParseUser.getCurrentUser();
        mIsSearching = mCurrentUser.getBoolean(ParseConstants.KEY_IS_SEARCHING);
        mCurrentUserMatchedRef = new Firebase(FirebaseConstants.URL_USERS)
                .child(mCurrentUser.getObjectId())
                .child(FirebaseConstants.KEY_MATCHED);
    }

    /** Set up Firebase event listener and Android location services */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_this_weekend, container, false);
        findViews(rootView);

        setUpEventListener();
        setUpViews();
        setUpLocation();

        return rootView;
    }

    /** Adds Firebase event listener */
    @Override
    public void onResume() {
        super.onResume();
        mIsSearching = mCurrentUser.getBoolean(ParseConstants.KEY_IS_SEARCHING);
        if (mIsSearching) {
            mCurrentUserMatchedRef.addValueEventListener(mValueEventListener);
        }
    }

    /** Removes Firebase event listener */
    @Override
    public void onPause() {
        super.onPause();
        mCurrentUserMatchedRef.removeEventListener(mValueEventListener);
    }

    /** Finds views */
    private void findViews(View rootView) {
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.this_weekend_linear_layout);
        mGoButton = (Button) rootView.findViewById(R.id.this_weekend_go_button);
        mHelpTextView = (TextView) rootView.findViewById(R.id.this_weekend_help);
        mSpinnerLayout = (LinearLayout) rootView.findViewById(R.id.this_weekend_spinner_layout);
    }

    /** Sets up Firebase event listener */
    private void setUpEventListener() {
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isMatched;

                // Check if user has been matched
                try {
                    isMatched = (Boolean) dataSnapshot.getValue();
                } catch (ClassCastException e) {
                    isMatched = false;
                }

                // If matched, remove event listener and call listener method
                if (isMatched) {
                    mCurrentUserMatchedRef.removeEventListener(mValueEventListener);
                    mListener.onMatchMade();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        };
    }

    /** Sets up appropriate views */
    private void setUpViews() {
        // Displays different views based on whether user is currently searching for match
        if (mIsSearching) {
            showProgressSpinner();
        } else {
            hideProgressSpinner();
            setUpGoButton();
        }

        // Sets up help button
        mHelpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mMainActivity);
                dialog.setContentView(R.layout.dialog_help);
                dialog.setTitle(R.string.this_weekend_dialog_help_title);
                Button button = (Button) dialog.findViewById(R.id.dialog_help_button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    /** Sets up location services */
    private void setUpLocation() {
        mLocationManager =
                (LocationManager) mMainActivity.getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            // Save user location to Parse
            @Override
            public void onLocationChanged(Location location) {
                mCurrentUser.put(ParseConstants.KEY_LATITUDE, location.getLatitude());
                mCurrentUser.put(ParseConstants.KEY_LONGITUDE, location.getLongitude());
                mCurrentUser.saveInBackground();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };
    }

    /** Sets up Go button */
    private void setUpGoButton() {
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save to Parse that user is searching
                mCurrentUser.put(ParseConstants.KEY_IS_SEARCHING, true);
                mCurrentUser.saveInBackground();

                // Show progress spinner and log location
                showProgressSpinner();
                mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, mLocationListener, null);

                // Add Firebase event listener
                mCurrentUserMatchedRef.addValueEventListener(mValueEventListener);
            }
        });
    }

    /** Shows progress spinner and hides Go button */
    private void showProgressSpinner() {
        mSpinnerLayout.setVisibility(View.VISIBLE);
        mLinearLayout.setVisibility(View.GONE);
    }

    /** Hides progress spinner and shows Go button */
    private void hideProgressSpinner() {
        mSpinnerLayout.setVisibility(View.GONE);
        mLinearLayout.setVisibility(View.VISIBLE);
    }

}