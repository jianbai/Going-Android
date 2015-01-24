package io.spw.hello;

/**
 * Created by scottwang on 12/28/14.
 * Based on Firebase template by @author greg
 */
public class Chat {

    private String message;
    private String author;
    private String time;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Chat() {
    }

    Chat(String message, String author, String time) {
        this.message = message;
        this.author = author;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public String getTime() {
        return time;
    }
}