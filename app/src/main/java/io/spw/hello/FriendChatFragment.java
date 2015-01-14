package io.spw.hello;

import android.app.Activity;
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
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by scottwang on 1/14/15.
 */
public class FriendChatFragment extends ListFragment {


    private static String mFriendChatId;

    private Activity mActivity;
    private String mUsername;
    private Firebase mFirebaseRef;
    private ChatListAdapter mChatListAdapter;

    private EditText mInputText;
    private ImageButton mSendButton;

    private ParseUser mCurrentUser;

    public FriendChatFragment(Activity c, String friendChatId) {
        mActivity = c;
        mFriendChatId = friendChatId;
        mFirebaseRef = new Firebase(FirebaseConstants.URL_FRIEND_CHATS)
                            .child(mFriendChatId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_chat, container, false);

        mCurrentUser = MainActivity.currentUser;

        findViews(rootView);
        setUpUsername();

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

        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limitToLast(50),
                getActivity(), R.layout.chat_message, mUsername, mActivity);
        listView.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(mChatListAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }

    private void findViews(View rootView) {
        mInputText = (EditText) rootView.findViewById(R.id.friend_chat_message_input);
        mSendButton = (ImageButton) rootView.findViewById(R.id.friend_chat_send_button);
    }

    private void setUpUsername() {
        if (mUsername == null) {
            mUsername = mCurrentUser.getString(ParseConstants.KEY_FIRST_NAME);
        }
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


}
