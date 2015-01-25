/**
 * Created by @author scott on 1/24/15.
 */

package io.spw.hello;

import android.app.AlertDialog;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
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

    private MainActivity mActivity;
    private MainPagerAdapter.MatchMadeDialogListener mListener;
    private ParseUser mCurrentUser;
    private List<ParseUser> mGroupMembers;
    private ParseRelation<ParseUser> mGroupMembersRelation;

    /** Initialize member variables and set up views */
    public MatchMadeDialog(MainActivity a, MainPagerAdapter.MatchMadeDialogListener listener) {
        super(a);
        setContentView(R.layout.dialog_match_made);
        setTitle(R.string.this_weekend_dialog_match_title);

        // Initialize member variables
        mListener = listener;
        mActivity = a;
        mCurrentUser = a.currentUser;
        mGroupMembersRelation =
                mCurrentUser.getRelation(ParseConstants.KEY_GROUP_MEMBERS_RELATION);

        // Try to update group members from Parse
        try {
            updateGroupMembers();
        } catch (JSONException | ParseException e) {
            showMatchErrorDialog();
        }

        // Set up TextViews and Button
        setUpTextViews();
        setUpButton();
    }

    /** Update list of group members */
    private void updateGroupMembers() throws JSONException, ParseException {
        mGroupMembers = new ArrayList<>();

        JSONArray userIds = mCurrentUser.getJSONArray(ParseConstants.KEY_MEMBER_IDS);
        for (int i=0; i<userIds.length(); i++) {
            if (!mCurrentUser.getObjectId().equals(userIds.getString(i))) {
                ParseUser user = ParseUser.getQuery().get(userIds.getString(i));
                mGroupMembers.add(user);
                mGroupMembersRelation.add(user);
            }
        }

        mCurrentUser.saveInBackground();
    }

    /** Sets up TextViews displaying match info in custom match dialog */
    private void setUpTextViews() {
        // Find views
        TextView userName0 = (TextView) findViewById(R.id.userName0);
        TextView userInfo0 = (TextView) findViewById(R.id.userInfo0);
        TextView userName1 = (TextView) findViewById(R.id.userName1);
        TextView userInfo1 = (TextView) findViewById(R.id.userInfo1);
        TextView userName2 = (TextView) findViewById(R.id.userName2);
        TextView userInfo2 = (TextView) findViewById(R.id.userInfo2);

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
        Button button = (Button) findViewById(R.id.dialog_hello_button);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.dialog_error_title)
                .setMessage(R.string.main_dialog_match_error_message)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog errorDialog = builder.create();
        errorDialog.show();
    }

}
