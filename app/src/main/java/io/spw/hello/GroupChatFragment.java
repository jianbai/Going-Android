package io.spw.hello;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scottwang on 1/8/15.
 */
public class GroupChatFragment extends Fragment {

    public static final String TAG = GroupChatFragment.class.getSimpleName();

    private TextView mUser1TextView;
    private TextView mUser2TextView;
    private TextView mUser3TextView;

    private ParseUser currentUser;
    private ParseObject parseGroup;
    private List<String> groupMemberIds;
    private List<ParseUser> groupMembers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_chat, container, false);

        findViews(rootView);

        currentUser = ParseUser.getCurrentUser();
        groupMembers = new ArrayList<ParseUser>();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_GROUPS);
        query.whereEqualTo(ParseConstants.KEY_MEMBER_IDS, currentUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject group, ParseException e) {
                if (e == null) {

                    parseGroup = group;
                    groupMemberIds = (ArrayList<String>) group.get(ParseConstants.KEY_MEMBER_IDS);

                    for (String memberId : groupMemberIds) {
                        if (!memberId.equals(currentUser.getObjectId())) {
                            ParseUser groupMember = null;

                            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
                            try {
                                groupMember = userQuery.get(memberId);
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                            groupMembers.add(groupMember);
                        }
                    }

                    mUser1TextView.setText(groupMembers.get(0).getString(ParseConstants.KEY_FIRST_NAME));
                    mUser2TextView.setText(groupMembers.get(1).getString(ParseConstants.KEY_FIRST_NAME));
                    mUser3TextView.setText(groupMembers.get(2).getString(ParseConstants.KEY_FIRST_NAME));
                }
            }
        });



        return rootView;
    }

    private void findViews(View rootView) {
        mUser1TextView = (TextView) rootView.findViewById(R.id.group_chat_member_1);
        mUser2TextView = (TextView) rootView.findViewById(R.id.group_chat_member_2);
        mUser3TextView = (TextView) rootView.findViewById(R.id.group_chat_member_3);
    }

}
