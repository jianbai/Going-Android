/**
 * Created by scott on 1/22/15.
 */

package io.spw.hello;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/** Provides fourth fragment of login tutorial */
public class LoginFragment3 extends Fragment {

    /** Displays login tutorial */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_3, container, false);
    }

}
