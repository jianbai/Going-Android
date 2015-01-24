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
 * Created by scottwang on 1/13/15.
 */
public class FriendChatActivity extends ActionBarActivity {

    public static final String TAG = FriendChatActivity.class.getSimpleName();

    protected static String mChatId;

    private ParseUser mCurrentUser;
    private ParseUser mFriend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);

        setTitle(getIntent().getStringExtra("friendName"));
        mChatId = getIntent().getStringExtra("chatId");
        mCurrentUser = MainActivity.currentUser;
        try {
            mFriend = ParseUser.getQuery().get(getIntent().getStringExtra("friendObjectId"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FriendChatFragment friendChatFragment = new FriendChatFragment(this, mChatId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.friend_chat_container, friendChatFragment)
                    .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        AlertDialog.Builder builder;

        switch(id) {
            case R.id.action_view_profile:

                final Dialog dialog = new Dialog(this);
                String friendObjectId = getIntent().getStringExtra("friendObjectId");

                dialog.setContentView(R.layout.dialog_view_profile);
                dialog.setTitle(R.string.title_view_profile);

                if (mFriend != null) {
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
                } else {
                    builder = new AlertDialog.Builder(FriendChatActivity.this);
                    builder.setTitle(R.string.title_network_error)
                            .setMessage(R.string.message_network_error)
                            .setPositiveButton(android.R.string.ok, null);

                    AlertDialog errorDialog = builder.create();
                    errorDialog.show();
                }

                break;
            case R.id.action_delete:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_delete_friend)
                        .setMessage(R.string.message_delete_friend)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ParseRelation<ParseUser> friendsRelation =
                                                mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
                                        if (mFriend != null) {
                                            friendsRelation.remove(mFriend);
                                            mCurrentUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    Toast.makeText(FriendChatActivity.this, R.string.toast_contact_deleted, Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            dialog.dismiss();
                                            NavUtils.navigateUpFromSameTask(FriendChatActivity.this);
                                        } else {
                                            // Show error dialog
                                        }
                                    }
                                });

                AlertDialog deleteDialog = builder.create();
                deleteDialog.show();
                break;
            case R.id.action_report:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.title_report_friend)
                        .setMessage(R.string.message_report_friend)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.button_report_friend,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ParseRelation<ParseUser> friendsRelation =
                                                mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
                                        if (mFriend != null) {
                                            mCurrentUser.add(ParseConstants.KEY_REPORTED, mFriend.getObjectId());
                                            friendsRelation.remove(mFriend);
                                            mCurrentUser.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    Toast.makeText(FriendChatActivity.this, R.string.toast_report_received, Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            dialog.dismiss();
                                            NavUtils.navigateUpFromSameTask(FriendChatActivity.this);
                                        } else {
                                            // Show error dialog
                                        }
                                    }
                                });

                AlertDialog reportDialog = builder.create();
                reportDialog.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
