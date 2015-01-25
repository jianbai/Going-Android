/**
 * Created by @author scottwang on 1/16/15.
 */

package io.spw.hello;

import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

/**
 * Provides a custom implementation of ParsePushBroadcastReceiver to
 * open DispatchActivity when user opens push notification
 */
public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {

    /** Starts an intent for DispatchActivity */
    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, DispatchActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
