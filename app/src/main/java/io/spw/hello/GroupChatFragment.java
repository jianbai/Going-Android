package io.spw.hello;

import android.content.Context;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * Created by scottwang on 1/8/15.
 */
public class GroupChatFragment extends ListFragment {

    public static final String TAG = GroupChatFragment.class.getSimpleName();

    private Context mContext;
    private String mUsername;
    private Firebase mFirebaseRef;
    private ChatListAdapter mChatListAdapter;

    private EditText mInputText;
    private ImageButton mSendButton;

    private ParseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        findViews(rootView);
        mContext = getActivity();
        currentUser = MainActivity.currentUser;

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

    @Override
    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }

    private void setUpUsername() {
//        SharedPreferences prefs = getActivity().getApplication().getSharedPreferences("ChatPrefs", 0);
//        mUsername = prefs.getString("username", null);
        if (mUsername == null) {
            mUsername = currentUser.getString(ParseConstants.KEY_FIRST_NAME);
//            prefs.edit().putString("username", mUsername).commit();
        }
    }

    private void setUpListView(final ListView listView) {
        mChatListAdapter = new ChatListAdapter(mFirebaseRef.limitToLast(50),
                getActivity(), R.layout.chat_message, mUsername, mContext);
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

}
