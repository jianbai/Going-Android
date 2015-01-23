package io.spw.hello;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONException;

/**
 * Created by scottwang on 12/20/14.
 */
public class ThisWeekendFragment extends Fragment {

    public static final String TAG = ThisWeekendFragment.class.getSimpleName();

    private Activity mainActivity;
    private MainPagerAdapter.ThisWeekendFragmentListener listener;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private LinearLayout mLinearLayout;
    private Button mGoButton;
    private TextView mHelpTextView;
    private LinearLayout mSpinnerLayout;

    private ParseUser currentUser;
    private Boolean isSearching;
    private Firebase currentUserMatchedRef;
    private ValueEventListener valueEventListener;

    public ThisWeekendFragment(Activity c, MainPagerAdapter.ThisWeekendFragmentListener listener) {
        mainActivity = c;
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
        setUpLocation();

        return rootView;
    }

    private void setUpLocation() {
        locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentUser.put(ParseConstants.KEY_LATITUDE, location.getLatitude());
                currentUser.put(ParseConstants.KEY_LONGITUDE, location.getLongitude());
                currentUser.saveInBackground();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        currentUserMatchedRef.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        currentUserMatchedRef.removeEventListener(valueEventListener);
    }

    private void findViews(View rootView) {
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.this_weekend_linear_layout);
        mGoButton = (Button) rootView.findViewById(R.id.this_weekend_go_button);
        mHelpTextView = (TextView) rootView.findViewById(R.id.this_weekend_help);
        mSpinnerLayout = (LinearLayout) rootView.findViewById(R.id.this_weekend_spinner_layout);
    }

    private void setUpViews() {
        if (isSearching) {
            showProgressSpinner();
            currentUserMatchedRef.addValueEventListener(valueEventListener);
        } else {
            hideProgressSpinner();
            setUpHelloButton();
        }

        mHelpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(mainActivity);
                dialog.setContentView(R.layout.dialog_faq);
                dialog.setTitle(R.string.dialog_faq_title);
                Button button = (Button) dialog.findViewById(R.id.dialog_faq_button);

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

    private void setUpHelloButton() {
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.put(ParseConstants.KEY_IS_SEARCHING, true);
                currentUser.saveInBackground();

                showProgressSpinner();
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);

                currentUserMatchedRef.addValueEventListener(valueEventListener);
            }
        });
    }

    private void showProgressSpinner() {
        mLinearLayout.setVisibility(View.GONE);
        mSpinnerLayout.setVisibility(View.VISIBLE);
    }

    private void hideProgressSpinner() {
        mLinearLayout.setVisibility(View.VISIBLE);
        mSpinnerLayout.setVisibility(View.GONE);
    }

    private void setUpEventListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isMatched;

                try {
                    isMatched = (Boolean) dataSnapshot.getValue();
                } catch (ClassCastException e) {
                    isMatched = false;
                }

                if (isMatched) {
                    currentUserMatchedRef.removeEventListener(valueEventListener);

                    try {
                        listener.onMatchMade();
                    } catch (JSONException | ParseException e) {
                        Log.d(TAG, e.getLocalizedMessage());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
    }

}