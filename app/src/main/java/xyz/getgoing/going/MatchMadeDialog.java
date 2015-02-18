/**
 * Created by @author scott on 1/24/15.
 */

package xyz.getgoing.going;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides custom Dialog for when a match is made
 */
public class MatchMadeDialog extends Dialog {

    private MainActivity mMainActivity;
    private MainPagerAdapter.MatchMadeDialogListener mListener;
    private ParseUser mCurrentUser;
    private List<ParseUser> mGroupMembers;
    private ParseRelation<ParseUser> mGroupMembersRelation;

    /** Initialize member variables and set up views */
    public MatchMadeDialog(MainActivity activity, MainPagerAdapter.MatchMadeDialogListener listener) {
        super(activity);
        setContentView(R.layout.dialog_match_made);
        setTitle(R.string.this_weekend_dialog_match_title);

        // Initialize member variables
        mListener = listener;
        mMainActivity = activity;
        mCurrentUser = ParseUser.getCurrentUser();
        mGroupMembersRelation =
                mCurrentUser.getRelation(ParseConstants.KEY_GROUP_MEMBERS_RELATION);
        mGroupMembers = new ArrayList<>();

        // Get group members from Parse
        fetchParseData();
    }

    /** Counts dialog as seen if user dismisses it */
    @Override
    public void onStop() {
        super.onStop();
        mListener.onMatchMadeDialogSeen();
    }

    /** Gets group member data from Parse */
    private void fetchParseData() {
        // Look for group containing current user
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_GROUPS);
        query.whereEqualTo(ParseConstants.KEY_GROUP_MEMBER_IDS, mCurrentUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject group, ParseException e) {
                handleParseData(group);
            }
        });
    }

    /** Adds group member info to mGroupMembers variable and ParseUser,
     * then sets up dialog textviews and button accordingly*/
    private void handleParseData(ParseObject group) {
        // Get list of member ids, add each member to mGroupMembers
        // and to mGroupMembersRelation
        JSONArray memberIds = group.getJSONArray(ParseConstants.KEY_GROUP_MEMBER_IDS);
        for (int i=0; i<memberIds.length(); i++) {
            try {
                String id = memberIds.getString(i);
                if (!mCurrentUser.getObjectId().equals(id)) {
                    ParseUser user = ParseUser.getQuery().get(id);
                    mGroupMembers.add(user);
                    mGroupMembersRelation.add(user);
                }
            } catch (JSONException | ParseException e1) {
                showMatchErrorDialog();
            }
        }

        // Add group id and save ParseUser
        mCurrentUser.put(ParseConstants.KEY_GROUP_ID, group.getObjectId());
        mCurrentUser.saveInBackground();

        // Set up TextViews and Button
        setUpTextViews();
        setUpButton();
    }

    /** Sets up TextViews displaying match info in custom match dialog */
    private void setUpTextViews() {
        // Find views
        TextView userName0 = (TextView) findViewById(R.id.user_name_0);
        TextView userInfo0 = (TextView) findViewById(R.id.user_info_0);
        TextView userName1 = (TextView) findViewById(R.id.user_name_1);
        TextView userInfo1 = (TextView) findViewById(R.id.user_info_1);
        TextView userName2 = (TextView) findViewById(R.id.user_name_2);
        TextView userInfo2 = (TextView) findViewById(R.id.user_info_2);

        // Set text
        userName0.setText(mGroupMembers.get(0).getString(ParseConstants.KEY_FIRST_NAME));
        userInfo0.setText(mGroupMembers.get(0).getString(ParseConstants.KEY_AGE) +
                "  : :  " +
                (mGroupMembers.get(0).getString(ParseConstants.KEY_HOMETOWN)));

        userName1.setText(mGroupMembers.get(1).getString(ParseConstants.KEY_FIRST_NAME));
        userInfo1.setText(mGroupMembers.get(1).getString(ParseConstants.KEY_AGE) +
                "  : :  " +
                (mGroupMembers.get(1).getString(ParseConstants.KEY_HOMETOWN)));

        userName2.setText(mGroupMembers.get(2).getString(ParseConstants.KEY_FIRST_NAME));
        userInfo2.setText(mGroupMembers.get(2).getString(ParseConstants.KEY_AGE) +
                "  : :  " +
                (mGroupMembers.get(2).getString(ParseConstants.KEY_HOMETOWN)));
    }

    /** Sets up button for custom match dialog */
    private void setUpButton() {
        // Find view
        Button button = (Button) findViewById(R.id.this_weekend_dialog_match_button);
        // Set click mListener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMatchMadeDialogSeen();
                dismiss();
            }
        });
    }

    /** Shows error dialog */
    private void showMatchErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setTitle(R.string.dialog_error_title)
                .setMessage(R.string.main_dialog_match_error_message)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog errorDialog = builder.create();
        errorDialog.show();
    }

}
