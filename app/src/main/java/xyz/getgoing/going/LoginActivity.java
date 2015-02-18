/**
 * Created by @author scottwang on 12/26/14.
 */

package xyz.getgoing.going;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.firebase.client.Firebase;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Displays tutorial and handles Facebook login
 */
public class LoginActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private ImageView mLoginPageIndicator;
    private Button mLoginButton;
    private ProgressBar mProgressSpinner;
    private Boolean mNoGender;
    private Boolean mNoAge;
    private Boolean mNoHometown;
    private ParseUser mCurrentUser;

    /** Sets up tutorial */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();

        setUpViewPager();
    }

    /** Authenticates Facebook login */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    /** Satisfies required method implementation for OnPageChangeListener */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    /** Changes page indicator image based on which page is selected */
    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mLoginPageIndicator.setImageResource(R.drawable.ic_login_indicator_0);
                break;
            case 1:
                mLoginPageIndicator.setImageResource(R.drawable.ic_login_indicator_1);
                break;
            case 2:
                mLoginPageIndicator.setImageResource(R.drawable.ic_login_indicator_2);
                break;
            case 3:
                mLoginPageIndicator.setImageResource(R.drawable.ic_login_indicator_3);
                break;
            default:
                break;
        }
    }

    /** Satisfies required method implementation for OnPageChangeListener */
    @Override
    public void onPageScrollStateChanged(int state) {}

    /** Finds views */
    private void findViews() {
        mViewPager = (ViewPager) findViewById(R.id.login_view_pager);
        mLoginPageIndicator = (ImageView) findViewById(R.id.login_page_indicator);
        mLoginButton = (Button) findViewById(R.id.login_facebook_button);
        mProgressSpinner = (ProgressBar) findViewById(R.id.login_progress_spinner);
    }

    /** Sets up tutorial ViewPager */
    private void setUpViewPager() {
        mViewPager.setAdapter(new LoginTutorialPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOnPageChangeListener(this);
    }

    /** Logs user into Parse and Facebook */
    public void onLoginButtonClicked(View v) {
        showProgressSpinner();

        List<String> permissions = Arrays.asList(
                FacebookConstants.PERMISSION_PUBLIC_PROFILE,
                FacebookConstants.PERMISSION_EMAIL,
                FacebookConstants.PERMISSION_USER_FRIENDS);

        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                mCurrentUser = ParseUser.getCurrentUser();

                // Handles result of login
                if (parseUser == null) {
                    // Login failed
                    Toast.makeText(LoginActivity.this, "user is null", Toast.LENGTH_SHORT).show();
                    showLoginErrorDialog();
                    hideProgressSpinner();
                } else if (parseUser.isNew()) {
                    // User is new
                    fetchFacebookData();
                } else {
                    // Login successful
                    navigateToMain();
                }
            }
        });
    }

    /** Fetches Facebook user data */
    private void fetchFacebookData() {
        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            makeMeRequest();
            makeMyFriendsRequest(session);
        }
    }

    /** Executes request for basic Facebook user info */
    private void makeMeRequest() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            // Update Parse user info with results
                            updateUserProfile(user);
//                            updateUserAge(user);
//                            updateUserHometown(user);
                            updateDefaultParseValues();

                            // Save user info
                            mCurrentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    // Hook user up to Firebase
                                    saveUserToFirebase();

                                    // Check if user profile is complete and navigate accordingly
                                    if (isUserProfileIncomplete()) {
                                        navigateToSetProfile();
                                    } else {
                                        navigateToMain();
                                    }
                                }
                            });
                        } else if (response.getError() != null) {
                            Toast.makeText(LoginActivity.this, "me request error", Toast.LENGTH_SHORT).show();
                            showLoginErrorDialog();
                            hideProgressSpinner();
                        }
                    }
                });

        request.executeAsync();
    }

    /** Executes request for Facebook friends info */
    private void makeMyFriendsRequest(Session session) {
        Request friendRequest = Request.newMyFriendsRequest(session,
                new Request.GraphUserListCallback(){
                    @Override
                    public void onCompleted(List<GraphUser> users,
                                            Response response) {
                        // Save a list of friends who use the app to Parse
                        for (GraphUser user : users) {
                            mCurrentUser.add(ParseConstants.KEY_FACEBOOK_FRIENDS,
                                    user.getId());
                        }
                        mCurrentUser.saveInBackground();
                    }
                });

        friendRequest.executeAsync();
    }

    /** Adds Facebook profile info to Parse User */
    private void updateUserProfile(GraphUser user) {
        // Add Facebook ID
        mCurrentUser.put(ParseConstants.KEY_FACEBOOK_ID, user.getId());

        // Add name
        mCurrentUser.put(ParseConstants.KEY_FIRST_NAME, user.getFirstName());
        mCurrentUser.put(ParseConstants.KEY_LAST_NAME, user.getLastName());

        // Add gender
        if (user.getProperty(ParseConstants.KEY_GENDER) != null) {
            mCurrentUser.put(ParseConstants.KEY_GENDER,
                    (String) user.getProperty(ParseConstants.KEY_GENDER));
        }

        // Add email
        if (user.getProperty(ParseConstants.KEY_EMAIL) != null) {
            mCurrentUser.put(ParseConstants.KEY_EMAIL,
                    (String) user.getProperty(ParseConstants.KEY_EMAIL));
        }
    }

    /** Adds Facebook age info to Parse User */
    private void updateUserAge(GraphUser user) {
        if (user.getBirthday() != null) {
            String birthday = (String) user.getBirthday();
            mCurrentUser.put(ParseConstants.KEY_BIRTHDAY, birthday);

            try {
                String age = calculateAge(birthday);
                mCurrentUser.put(ParseConstants.KEY_AGE, age);
            } catch (java.text.ParseException e) {
                // If exception prevents any user field from being automatically
                // added, LoginActivity will navigate to SetProfileActivity and
                // user will be prompted to manually enter missing fields
            }

        }
    }

    /** Adds Facebook hometown info to ParseUser */
    private void updateUserHometown(GraphUser user) {
        if (user.getProperty(ParseConstants.KEY_HOMETOWN) != null) {
            JSONObject h =
                    (JSONObject) user.getProperty(ParseConstants.KEY_HOMETOWN);

            try {
                mCurrentUser.put(ParseConstants.KEY_HOMETOWN,
                        h.getString(ParseConstants.KEY_HOMETOWN_NAME));
            } catch (JSONException e) {
                // If exception prevents any user field from being automatically
                // added, LoginActivity will navigate to SetProfileActivity and
                // user will be prompted to manually enter missing fields
            }

        }
    }

    /** Adds default values for Parse Booleans to ParseUser */
    private void updateDefaultParseValues() {
        mCurrentUser.put(ParseConstants.KEY_IS_MATCHED, false);
        mCurrentUser.put(ParseConstants.KEY_IS_SEARCHING, false);
        mCurrentUser.put(ParseConstants.KEY_MATCH_DIALOG_SEEN, false);
        mCurrentUser.put(ParseConstants.KEY_PICK_FRIENDS_DIALOG_SEEN, true);
        mCurrentUser.put(ParseConstants.KEY_GENDER_SETTINGS, 0);
        mCurrentUser.put(ParseConstants.KEY_AGE_SETTINGS_0, true);
        mCurrentUser.put(ParseConstants.KEY_AGE_SETTINGS_20, false);
        mCurrentUser.put(ParseConstants.KEY_AGE_SETTINGS_30, false);
        mCurrentUser.put(ParseConstants.KEY_AGE_SETTINGS_40, false);
    }

    /** Returns a String with age calculated from birthday */
    private String calculateAge(String birthday) throws java.text.ParseException {
        // Create two calendars with date of birthday and current date
        Date date = new SimpleDateFormat("MM/dd/yyyy").parse(birthday);
        Date now = new Date();
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(date);
        cal2.setTime(now);

        // Check if birthday has passed this year
        int factor = 0;
        if (cal2.get(Calendar.DAY_OF_YEAR) < cal1.get(Calendar.DAY_OF_YEAR)) {
            factor = -1;
        }

        // Calculate difference in years, then subtract 1 if birthday has not yet passed this year
        int age = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR) + factor;

        return String.valueOf(age);
    }

    /** Saves relevant user info to Firebase */
    private void saveUserToFirebase() {
        Firebase usersRef = new Firebase(FirebaseConstants.URL_USERS);

        String parseId = mCurrentUser.getObjectId();
        String fullName = mCurrentUser.getString(ParseConstants.KEY_FIRST_NAME)
                + " " + mCurrentUser.getString(ParseConstants.KEY_LAST_NAME);

        usersRef.child(parseId).child(FirebaseConstants.KEY_FULL_NAME).setValue(fullName);
        usersRef.child(parseId).child(FirebaseConstants.KEY_MATCHED).setValue(false);
    }

    /** Returns true if all user info has successfully been saved to Parse, false otherwise */
    private Boolean isUserProfileIncomplete() {
        mNoGender = mCurrentUser.getString(ParseConstants.KEY_GENDER) == null;
        mNoAge = mCurrentUser.getString(ParseConstants.KEY_AGE) == null;
        mNoHometown = mCurrentUser.getString(ParseConstants.KEY_HOMETOWN) == null;

        return (mNoGender || mNoAge || mNoHometown);
    }

    /** Navigates to SetProfileActivity with extra booleans indicating which fields are missing */
    private void navigateToSetProfile() {
        Intent intent = new Intent(this, SetProfileActivity.class);
        intent.putExtra("noGender", mNoGender);
        intent.putExtra("noAge", mNoAge);
        intent.putExtra("noHometown", mNoHometown);
        startActivity(intent);
        finish();
    }

    /** Navigates to MainActivity */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /** Shows error dialog */
    private void showLoginErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.login_dialog_error_message)
                .setTitle(R.string.dialog_error_title)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /** Shows progress spinner and hides login button */
    private void showProgressSpinner() {
        mLoginButton.setVisibility(View.GONE);
        mProgressSpinner.setVisibility(View.VISIBLE);
    }

    /** Hides progress spinner and shows login button */
    private void hideProgressSpinner() {
        mProgressSpinner.setVisibility(View.GONE);
        mLoginButton.setVisibility(View.VISIBLE);
    }

}