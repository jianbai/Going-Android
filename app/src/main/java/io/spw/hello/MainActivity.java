package io.spw.hello;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class MainActivity extends ActionBarActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "loaded main activity");

//        ParseUser currentUser = ParseUser.getCurrentUser();
//        if ((currentUser == null) || !ParseFacebookUtils.isLinked(currentUser)) {
//            navigateToLogin();
//        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SectionsPagerAdapter(this, getSupportFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, R.id.tab_title);
        mSlidingTabLayout.setViewPager(mViewPager);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.commit();
        }

        // Fetch Facebook user info if session is active
        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            makeMeRequest();
        }
    }

    private void makeMeRequest() {
        Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        if (user != null) {
                            ParseUser currentUser = ParseUser.getCurrentUser();

                            currentUser.put("facebookId", user.getId());
                            currentUser.put("firstName", user.getFirstName());
                            currentUser.put("lastName", user.getLastName());

                            if (user.getProperty("gender") != null) {
                                currentUser.put("gender",
                                        (String) user.getProperty("gender"));
                            }
                            if (user.getProperty("hometown") != null) {
                                currentUser.put("hometown", user.getProperty("hometown"));
                            }
                            if (user.getBirthday() != null) {
                                currentUser.put("birthday", (String) user.getBirthday());
                            }
                            if (user.getProperty("email") != null) {
                                currentUser.put("email",
                                        (String) user.getProperty("email"));
                            }

                            // Save user info
                            currentUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(MainActivity.this, "Saved!", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else if (response.getError() != null) {
                            Log.d(TAG, response.getError().getErrorMessage());
                            // TODO: handle this error
                        }
                    }
                });
        request.executeAsync();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_logout:
                //ParseFacebookUtils.getSession().closeAndClearTokenInformation();
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_settings:
                navigateToSettings();
                break;
        }

        return true;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

}
