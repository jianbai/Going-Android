/**
 * Created by @author scottwang on 1/13/15.
 */

package io.spw.hello;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Provides activity containing FriendChatFragments
 */
public class FriendChatActivity extends ActionBarActivity {

    public ParseUser currentUser;
    private ParseUser mFriend;
    private ParseRelation<ParseUser> mFriendsRelation;

    /** Initializes member variables and adds FriendChatFragment to layout */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);
        setTitle(getIntent().getStringExtra("friendName"));
        currentUser = ParseUser.getCurrentUser();
        mFriendsRelation =
                currentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        try {
            mFriend = ParseUser.getQuery().get(getIntent().getStringExtra("friendObjectId"));
        } catch (ParseException e) {
            // If there is a problem loading a ParseUser into mFriend, an error dialog will be
            // shown if the user tries to delete or report the friend
        }

        FriendChatFragment friendChatFragment =
                new FriendChatFragment(this, getIntent().getStringExtra("chatId"));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.friend_chat_fragment_container, friendChatFragment).commit();
    }

    /** Inflates menu */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    /** Handles click events for each cell in menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mFriend == null) {
            showErrorDialog();
            return super.onOptionsItemSelected(item);
        }

        switch(item.getItemId()) {
            case R.id.action_view_profile:
                showViewProfileDialog();
                break;
            case R.id.action_delete:
                showDeleteDialog();
                break;
            case R.id.action_report:
                showReportDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Shows view profile dialog */
    private void showViewProfileDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_view_profile);
        dialog.setTitle(R.string.friend_chat_dialog_view_profile_title);

        String friendName = mFriend.getString(ParseConstants.KEY_FIRST_NAME);
        String friendAge = mFriend.getString(ParseConstants.KEY_AGE);
        String friendHometown = mFriend.getString(ParseConstants.KEY_HOMETOWN);

        TextView friendNameTextView = (TextView) dialog.findViewById(R.id.friend_name);
        TextView friendInfoTextView = (TextView) dialog.findViewById(R.id.friend_info);

        friendNameTextView.setText(friendName);
        friendInfoTextView.setText(friendAge + "  : :  " + friendHometown);

        Button okButton = (Button) dialog.findViewById(R.id.dialog_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /** Shows delete dialog */
    private void showDeleteDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.friend_chat_dialog_delete_title)
                .setMessage(R.string.friend_chat_dialog_delete_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.friend_chat_dialog_delete_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteFriend(dialog, R.string.friend_chat_toast_contact_deleted);
                            }
                        });

        AlertDialog deleteDialog = builder.create();
        deleteDialog.show();
    }

    /** Shows report dialog */
    private void showReportDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.friend_chat_dialog_report_title)
                .setMessage(R.string.friend_chat_dialog_report_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.friend_chat_dialog_report_button,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                currentUser.add(ParseConstants.KEY_REPORTED, mFriend.getObjectId());
                                deleteFriend(dialog, R.string.friend_chat_toast_report_received);
                            }
                        });

        AlertDialog reportDialog = builder.create();
        reportDialog.show();
    }

    /** Deletes friend, saves to Parse and navigates back to MainActivity */
    private void deleteFriend(DialogInterface dialog, final int messageId) {
        mFriendsRelation.remove(mFriend);
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(FriendChatActivity.this, messageId, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.dismiss();
        NavUtils.navigateUpFromSameTask(FriendChatActivity.this);
    }

    /** Shows error dialog */
    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_error_title)
                .setMessage(R.string.dialog_error_message)
                .setPositiveButton(android.R.string.ok, null);

        AlertDialog errorDialog = builder.create();
        errorDialog.show();
    }

}
