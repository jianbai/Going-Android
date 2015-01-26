/**
 * Created by @author scottwang on 1/8/15.
 */

package io.spw.hello;

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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Provides Group Chat page
 */
public class GroupChatFragment extends ListFragment {

    private MainActivity mMainActivity;
    private MainPagerAdapter.GroupChatFragmentListener mListener;
    private String mUsername;
    private Firebase mGroupChatRef;
    private ChatListAdapter mChatListAdapter;
    private EditText mInputText;
    private ImageButton mSendButton;
    private ParseUser mCurrentUser;

    /** Initializes member variables */
    public GroupChatFragment(MainActivity activity,
                             MainPagerAdapter.GroupChatFragmentListener listener) {
        mMainActivity = activity;
        mListener = listener;
        mCurrentUser = ParseUser.getCurrentUser();
        if (mUsername == null) {
            mUsername = mCurrentUser.getString(ParseConstants.KEY_FIRST_NAME);
        }
    }

    /** Sets up message input and send button */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        findViews(rootView);

        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL &&
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
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

    /** Sets up Firebase event listener and ListView */
    @Override
    public void onStart() {
        super.onStart();

        // Check if match expired
        if (mCurrentUser.getBoolean(ParseConstants.KEY_IS_MATCHED)) {
            setUpSingleEventListener();
        }

        // Setup view and list adapter
        final ListView listView = getListView();
        // Initialize mGroupChatRef if it is null
        if (mGroupChatRef == null) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_GROUPS);
            query.whereEqualTo(ParseConstants.KEY_GROUP_MEMBER_IDS, mCurrentUser.getObjectId());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject group, ParseException e) {
                    mGroupChatRef = new Firebase(FirebaseConstants.URL_GROUP_CHATS)
                            .child(group.getObjectId());
                    setUpListView(listView);
                }
            });
        } else {
            setUpListView(listView);
        }
    }

    /** Removes all Firebase event listeners */
    @Override
    public void onStop() {
        super.onStop();
        if (mChatListAdapter != null) {
            mChatListAdapter.cleanup();
        }
    }

    /** Finds views */
    private void findViews(View rootView) {
        mInputText = (EditText) rootView.findViewById(R.id.group_chat_message_input);
        mSendButton = (ImageButton) rootView.findViewById(R.id.group_chat_send_button);
    }

    /** Adds Firebase event listener to check if match is expired */
    private void setUpSingleEventListener() {
        mMainActivity.currentUserRef.child(FirebaseConstants.KEY_MATCHED)
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
                            mListener.onMatchExpired();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {}
                });
    }

    /** Fills ListView with messages from Firebase */
    private void setUpListView(final ListView listView) {
        mChatListAdapter = new ChatListAdapter(mGroupChatRef.limitToLast(50),
                getActivity(), R.layout.custom_chat_bubble, mUsername, mMainActivity);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });
    }

    /** Sends a message to Firebase */
    private void sendMessage() {
        String input = mInputText.getText().toString();
        if (!input.equals("")) {
            // Get current time
            SimpleDateFormat dateFormatter = new SimpleDateFormat("h.mm a");
            GregorianCalendar cal = new GregorianCalendar();
            String time = dateFormatter.format(cal.getTime());

            // Create our 'model', a Chat object
            Chat chat = new Chat(input, mUsername, time);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mGroupChatRef.push().setValue(chat);
            mInputText.setText("");
        }
    }

}
