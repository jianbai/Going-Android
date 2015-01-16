package io.spw.hello;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by scottwang on 1/13/15.
 */
public class FriendChatActivity extends ActionBarActivity {

    protected static String mFriendChatId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_chat);

        mFriendChatId = getIntent().getStringExtra("friendChatId");

        FriendChatFragment friendChatFragment = new FriendChatFragment(this, mFriendChatId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.friend_chat_container, friendChatFragment)
                    .commit();

    }



}
