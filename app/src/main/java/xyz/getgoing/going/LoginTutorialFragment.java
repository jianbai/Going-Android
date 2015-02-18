/**
 * Created by @author scott on 1/22/15.
 */

package xyz.getgoing.going;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Provides fragment of login tutorial based on given layout ID
 */
public class LoginTutorialFragment extends Fragment {

    private int mLayoutId;

    public LoginTutorialFragment() {
        super();
    }

    public LoginTutorialFragment(int layoutId) {
        mLayoutId = layoutId;
    }

    /** Displays login tutorial */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(mLayoutId, container, false);
    }

}
