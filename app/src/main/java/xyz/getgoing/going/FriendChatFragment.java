/**
 * Created by @author scottwang on 1/14/15.
 */

package xyz.getgoing.going;

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

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Provides fragment for friend chats
 */
public class FriendChatFragment extends ListFragment {

    private FriendChatActivity mActivity;
    private String mUsername;
    private Firebase mFriendChatRef;
    private ChatListAdapter mChatListAdapter;
    private EditText mInputText;
    private ImageButton mSendButton;

    /** Initializes member variables */
    public FriendChatFragment(FriendChatActivity activity, String friendChatId) {
        mActivity = activity;
        mFriendChatRef = new Firebase(FirebaseConstants.URL_FRIEND_CHATS).child(friendChatId);
        if (mUsername == null) {
            mUsername = mActivity.currentUser.getString(ParseConstants.KEY_FIRST_NAME);
        }
    }

    /** Sets up message input and send button */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_chat, container, false);
        findViews(rootView);

        // Set up message input EditText
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

        // Set up send button
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        return rootView;
    }

    /** Fills ListView with messages from Firebase */
    @Override
    public void onStart() {
        super.onStart();
        final ListView listView = getListView();

        mChatListAdapter = new ChatListAdapter(mFriendChatRef.limitToLast(50),
                getActivity(), R.layout.custom_chat_bubble, mUsername, mActivity);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });
    }

    /** Removes all Firebase event listeners */
    @Override
    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }

    /** Finds views */
    private void findViews(View rootView) {
        mInputText = (EditText) rootView.findViewById(R.id.friend_chat_message_input);
        mSendButton = (ImageButton) rootView.findViewById(R.id.friend_chat_send_button);
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
            mFriendChatRef.push().setValue(chat);
            mInputText.setText("");
        }
    }

}
