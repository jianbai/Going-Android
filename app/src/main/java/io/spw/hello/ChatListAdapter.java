/**
 * Created by @author scottwang on 12/28/14.
 * Based on Firebase template by @author greg
 */

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
 * Provides custom ListAdapter implementation for Firebase chat
 */
public class ChatListAdapter extends FirebaseListAdapter<Chat> {

    public static final int DEFAULT_WHITE = 0xFFF6F6EF;
    private String mUsername;
    private Context mContext;

    /** Initializes member variables */
    public ChatListAdapter(Query ref, Activity activity, int layout, String mUsername, Context mContext) {
        super(ref, Chat.class, layout, activity);
        this.mUsername = mUsername;
        this.mContext = mContext;
    }

    /** Populates each ListView cell with a Chat object */
    @Override
    protected void populateView(View view, Chat chat) {
        // Map a Chat object to an entry in our listview
        String author = chat.getAuthor();
        String time = chat.getTime();
        String message = chat.getMessage();

        // Set authorText with author name and timestamp
        TextView authorText = (TextView) view.findViewById(R.id.author);
        authorText.setAllCaps(true);
        authorText.setText(author + "  : :  " + time);

        // Set messageText with message
        TextView messageText = (TextView) view.findViewById(R.id.message);
        messageText.setText(message);

        styleMessage(view, author, messageText);
    }

    /** Styles each chat bubble */
    private void styleMessage(View view, String author, TextView messageText) {
        // Find views
        LinearLayout authorContainer = (LinearLayout) view.findViewById(R.id.authorContainer);
        LinearLayout messageContainer = (LinearLayout) view.findViewById(R.id.messageContainer);

        // Create layout params
        Resources r = mContext.getResources();
        int DEFAULT_TEXT_COLOR = r.getColor(R.color.abc_primary_text_material_light);
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                48,
                r.getDisplayMetrics()
        );
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        // Style message differently if author is user
        if (author.equals(mUsername)) {
            // If author is user, align right and use orange chat bubble
            authorContainer.setGravity(Gravity.RIGHT);
            messageContainer.setGravity(Gravity.RIGHT);
            params.setMargins(px, 0, 0, 0);
            messageText.setLayoutParams(params);
            messageText.setBackgroundResource(R.drawable.bubble_orange);
            messageText.setTextColor(DEFAULT_WHITE);
        } else {
            // Otherwise, align left and use grey chat bubble
            authorContainer.setGravity(Gravity.LEFT);
            messageContainer.setGravity(Gravity.LEFT);
            params.setMargins(0, 0, px, 0);
            messageText.setLayoutParams(params);
            messageText.setBackgroundResource(R.drawable.bubble_grey);
            messageText.setTextColor(DEFAULT_TEXT_COLOR);
        }
    }

}
