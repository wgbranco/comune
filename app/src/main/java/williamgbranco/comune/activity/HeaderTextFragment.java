package williamgbranco.comune.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import williamgbranco.comune.R;


public class HeaderTextFragment extends Fragment
{
    public static final String TAG = "comune.HeaderTextFrag";
    public static final String EXTRA_MSG_ID = "HeaderTextFragment.message_id";

    private Integer mHeaderMsgId;
    private TextView mTextViewHeaderMsg;


    public static HeaderTextFragment newInstance(int msgId)
    {
        HeaderTextFragment fragment = new HeaderTextFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_MSG_ID, msgId);
        fragment.setArguments(args);

        return fragment;
    }

    public HeaderTextFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null)
        {
            mHeaderMsgId = args.getInt(EXTRA_MSG_ID, -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_header_text, container, false);

        mTextViewHeaderMsg = (TextView) v.findViewById(R.id.textview_header_msg);
        if (mHeaderMsgId != null)
            mTextViewHeaderMsg.setText(mHeaderMsgId);

        return v;
    }

}
