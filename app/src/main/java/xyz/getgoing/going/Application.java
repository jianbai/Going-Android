/**
 * Created by @author scottwang on 12/28/14.
 * Facebook app keys:
 * ga0RGNYHvNM5d0SLGQfpQWAPGJ8=
 * n+xcAaOIG1e1XpxStAc4PkDDnXM=
 * b9NiCI/tkmusUAAs4aW1LCFk9Uw=
 */

package xyz.getgoing.going;

import com.firebase.client.Firebase;
import com.parse.Parse;
import com.parse.ParseCrashReporting;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;

/**
 * Initializes 3rd party services
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Parse Local Datastore.
        Parse.enableLocalDatastore(this);

        // Enable Parse Crash Reporting
        ParseCrashReporting.enable(this);

        // Initialize Parse
        Parse.initialize(this, "BgVWp9cm22GjjGzt6Qj9v9TDYaAQaCIYR6Fe8y2j",
                "FPQS5IsBfyNv4CJz5DH4FlCUtELeIeJkMbE6Q6g3");
        ParseFacebookUtils.initialize("748438291904311");

        // Initialize Firebase
        Firebase.setAndroidContext(this);

        // Initialize Parse Push Notifications
        ParsePush.subscribeInBackground("");
    }

}
