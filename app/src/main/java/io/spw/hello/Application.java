package io.spw.hello;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by scottwang on 12/28/14.
 */
public class Application extends android.app.Application {

    public Application() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "BgVWp9cm22GjjGzt6Qj9v9TDYaAQaCIYR6Fe8y2j", "FPQS5IsBfyNv4CJz5DH4FlCUtELeIeJkMbE6Q6g3");
        ParseFacebookUtils.initialize("748438291904311");

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // TODO: Enable analytics
    }



}
