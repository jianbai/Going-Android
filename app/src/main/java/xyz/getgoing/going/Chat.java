/**
 * Created by scottwang on 12/28/14.
 * Based on Firebase template by @author greg
 */

package xyz.getgoing.going;

/**
 * Provides class for chat messages
 */
public class Chat {

    private String message;
    private String author;
    private String time;

    /** Provides required default constructor for Firebase object mapping */
    @SuppressWarnings("unused")
    private Chat() {
    }

    /** Initializes member variables */
    Chat(String message, String author, String time) {
        this.message = message;
        this.author = author;
        this.time = time;
    }

    /** Returns message field */
    public String getMessage() {
        return message;
    }

    /** Returns author field */
    public String getAuthor() {
        return author;
    }

    /** Returns time field */
    public String getTime() {
        return time;
    }
}