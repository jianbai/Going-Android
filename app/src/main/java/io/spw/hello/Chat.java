/**
 * Created by scottwang on 12/28/14.
 * Based on Firebase template by @author greg
 */

package io.spw.hello;

/**
 * Provides class for chat messages
 */
public class Chat {

    private String mMessage;
    private String mAuthor;
    private String mTime;

    /** Provides required default constructor for Firebase object mapping */
    @SuppressWarnings("unused")
    private Chat() {
    }

    /** Initializes member variables */
    Chat(String message, String author, String time) {
        mMessage = message;
        mAuthor = author;
        mTime = time;
    }

    /** Returns message field */
    public String getMessage() {
        return mMessage;
    }

    /** Returns author field */
    public String getAuthor() {
        return mAuthor;
    }

    /** Returns time field */
    public String getTime() {
        return mTime;
    }

}