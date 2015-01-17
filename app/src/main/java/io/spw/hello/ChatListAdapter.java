package io.spw.hello;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.Query;

/**
 * @author greg
 * @since 6/21/13
 *
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>Chat</code> class to encapsulate the
 * data for each individual chat message
 */
public class ChatListAdapter extends FirebaseListAdapter<Chat> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String mUsername;
    private Context mContext;
    private static final int DEFAULT_WHITE = 0xFFF6F6EF;

    public ChatListAdapter(Query ref, Activity activity, int layout, String mUsername, Context mContext) {
        super(ref, Chat.class, layout, activity);
        this.mUsername = mUsername;
        this.mContext = mContext;
    }

    /**
     * Bind an instance of the <code>Chat</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>Chat</code> instance that represents the current data to bind.
     *
     * @param view A view instance corresponding to the layout we passed to the constructor.
     * @param chat An instance representing the current state of a chat message
     */
    @Override
    protected void populateView(View view, Chat chat) {
        // Map a Chat object to an entry in our listview
        String author = chat.getAuthor();
        String time = chat.getTime();
        String message = chat.getMessage();

        TextView authorText = (TextView) view.findViewById(R.id.author);
        LinearLayout authorContainer = (LinearLayout) view.findViewById(R.id.authorContainer);
        authorText.setAllCaps(true);
        authorText.setText(author + "  : :  " + time);

        TextView messageText = (TextView) view.findViewById(R.id.message);
        LinearLayout messageContainer = (LinearLayout) view.findViewById(R.id.messageContainer);
        messageText.setText(message);
        // If message is from current user, align right
        Resources r = mContext.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                48,
                r.getDisplayMetrics()
        );

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int DEFAULT_TEXT = r.getColor(R.color.abc_primary_text_material_light);

        if (author != null && author.equals(mUsername)) {
            authorContainer.setGravity(Gravity.RIGHT);
            messageContainer.setGravity(Gravity.RIGHT);
            params.setMargins(px, 0, 0, 0);
            messageText.setLayoutParams(params);
            messageText.setBackgroundResource(R.drawable.bubble_orange);
            messageText.setTextColor(DEFAULT_WHITE);
        } else {
            authorContainer.setGravity(Gravity.LEFT);
            messageContainer.setGravity(Gravity.LEFT);
            params.setMargins(0, 0, px, 0);
            messageText.setLayoutParams(params);
            messageText.setBackgroundResource(R.drawable.bubble_grey);
            messageText.setTextColor(DEFAULT_TEXT);
        }
    }
}
