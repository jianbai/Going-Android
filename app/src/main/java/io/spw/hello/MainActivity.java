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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class MainActivity extends ActionBarActivity {

    public final static String TAG = MainActivity.class.getSimpleName();

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private ParseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentUser = ParseUser.getCurrentUser();

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
                            currentUser.put("facebookId", user.getId());
                            currentUser.put("firstName", user.getFirstName());
                            currentUser.put("lastName", user.getLastName());

                            if (user.getProperty("gender") != null) {
                                currentUser.put("gender",
                                        (String) user.getProperty("gender"));
                            }
                            if (user.getProperty("hometown") != null) {
                                JSONObject h = (JSONObject) user.getProperty("hometown");

                                try {
                                    currentUser.put("hometown", h.getString("name"));
                                } catch (JSONException e) {
                                    Log.d(TAG, e.getLocalizedMessage());
                                }

                            }
                            if (user.getBirthday() != null) {
                                String birthday = (String) user.getBirthday();
                                currentUser.put("birthday", birthday);

                                try {
                                    String age = calculateAge(birthday);
                                    currentUser.put("age", age);
                                } catch (java.text.ParseException e) {
                                    Log.d(TAG, e.getLocalizedMessage());
                                }

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

                                    // Check for required info
                                    checkUserInfo();
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

    private void checkUserInfo() {
        Boolean noGender = currentUser.getString("gender") == null;
        Boolean noAge = currentUser.getString("age") == null;
        Boolean noHometown = currentUser.getString("hometown") == null;

        if (noGender || noAge || noHometown) {
            Intent intent = new Intent(this, SetProfileActivity.class);
            intent.putExtra("noGender", noGender);
            intent.putExtra("noAge", noAge);
            intent.putExtra("noHometown", noHometown);
            startActivity(intent);
        }
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

    private String calculateAge(String birthday) throws java.text.ParseException {
        int age;

        Date date = new SimpleDateFormat("MM/dd/yyyy").parse(birthday);
        Date now = new Date();

        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(date);
        cal2.setTime(now);

        int factor = 0;

        if (cal2.get(Calendar.DAY_OF_YEAR) < cal1.get(Calendar.DAY_OF_YEAR)) {
            factor = -1;
        }

        age = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR) + factor;

        return String.valueOf(age);
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
