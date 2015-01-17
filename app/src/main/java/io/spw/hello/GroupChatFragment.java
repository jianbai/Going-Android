package io.spw.hello;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by scottwang on 1/8/15.
 */
public class GroupChatFragment extends ListFragment {

    public static final String TAG = GroupChatFragment.class.getSimpleName();

    private SectionsPagerAdapter.GroupChatFragmentListener listener;

    private Activity mainActivity;
    private String mUsername;
    private Firebase mFirebaseRef;
    private ChatListAdapter mChatListAdapter;

    private EditText mInputText;
    private ImageButton mSendButton;

    private ParseUser currentUser;
    private ParseRelation<ParseUser> mFriendsRelation;
    private ParseRelation<ParseUser> mGroupMembersRelation;
    private List<ParseUser> groupMembers;

    private static boolean[] mFriendsToKeep = {
            false, false, false
    };

    public GroupChatFragment(Activity a, SectionsPagerAdapter.GroupChatFragmentListener listener) {
        mainActivity = a;
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        findViews(rootView);
        currentUser = MainActivity.currentUser;
        mFriendsRelation = currentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        setUpUsername();
        if (currentUser.getBoolean(ParseConstants.KEY_IS_MATCHED)) {
            setUpSingleEventListener();
        }

        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    sendMessage();
                }
                return true;
            }
        });

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Setup view and list adapter
        final ListView listView = getListView();

        if (mFirebaseRef == null) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_GROUPS);
            query.whereEqualTo(ParseConstants.KEY_MEMBER_IDS, currentUser.getObjectId());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject group, ParseException e) {
                    mFirebaseRef = new Firebase(FirebaseConstants.URL_GROUP_CHATS)
                            .child(group.getObjectId());
                    setUpListView(listView);
                }
            });
        } else {
            setUpListView(listView);
        }
    }

    private void setUpSingleEventListener() {
        MainActivity.currentUserRef.child(FirebaseConstants.KEY_MATCHED)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Boolean isMatched;

                        try {
                            isMatched = (Boolean) dataSnapshot.getValue();
                        } catch (ClassCastException e) {
                            isMatched = true;
                        }

                        if (!isMatched) {
                            showPickFriendsDialog();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }

    private void setUpUsername() {
        if (mUsername == null) {
            mUsername = currentUser.getString(ParseConstants.KEY_FIRST_NAME);
        }
    }

    private void setUpListView(final ListView listView) {
        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limitToLast(50),
                getActivity(), R.layout.chat_message, mUsername, mainActivity);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });
    }
    private void findViews(View rootView) {
        mInputText = (EditText) rootView.findViewById(R.id.group_chat_message_input);
        mSendButton = (ImageButton) rootView.findViewById(R.id.group_chat_send_button);
    }

    private void sendMessage() {
        String input = mInputText.getText().toString();
        if (!input.equals("")) {
            // Get current time
            SimpleDateFormat dateFormatter = new SimpleDateFormat("h:mm a");
            GregorianCalendar cal = new GregorianCalendar();
            String time = dateFormatter.format(cal.getTime());

            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername, time);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.push().setValue(chat);
            mInputText.setText("");
        }
    }

    private void showPickFriendsDialog() {
        mGroupMembersRelation =
                currentUser.getRelation(ParseConstants.KEY_GROUP_MEMBERS_RELATION);

        ParseQuery<ParseUser> query = mGroupMembersRelation.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    groupMembers = parseUsers;

                    String[] names = new String[groupMembers.size()];
                    for (int i=0; i<groupMembers.size(); i++) {
                        if (!groupMembers.get(i).getObjectId()
                                .equals(currentUser.getObjectId())) {
                            names[i] = groupMembers.get(i).getString(ParseConstants.KEY_FIRST_NAME);
                        }
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
                    builder.setTitle(getString(R.string.dialog_pick_friends_title))
                            .setMultiChoiceItems(names, mFriendsToKeep,
                                    new DialogInterface.OnMultiChoiceClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which,
                                                            boolean isChecked) {
                                            mFriendsToKeep[which] = isChecked;
                                        }
                                    })
                            .setPositiveButton(getString(R.string.dialog_pick_friends_button),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            for (int i=0; i<groupMembers.size(); i++) {
                                                mGroupMembersRelation.remove(groupMembers.get(i));
                                                if (mFriendsToKeep[i]) {
                                                    mFriendsRelation.add(groupMembers.get(i));
                                                }
                                            }
                                            currentUser.put(ParseConstants.KEY_IS_MATCHED, false);
                                            currentUser.put(ParseConstants.KEY_MATCH_DIALOG_SEEN, false);
                                            currentUser.saveInBackground();
                                            ParsePush.unsubscribeInBackground("group" +
                                                    currentUser.getString(ParseConstants.KEY_GROUP_ID));
                                            listener.onFriendsPicked();
                                        }
                                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

}
