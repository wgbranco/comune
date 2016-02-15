package williamgbranco.comune.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.GetPublicInstitutionCallbacks;
import williamgbranco.comune.institution.PublicInstitution;
import williamgbranco.comune.manager.PublicInstitutionManager;

public class HeaderTitleFragment extends Fragment
{
    public static final String TAG = "comune.HeaderTitleFrag";

    public static final String EXTRA_MSG_ID = "HeaderTitleFragment.message_id";
    public static final String EXTRA_INST_ID = "HeaderTitleFragment.institution_id";
    public static final String EXTRA_DISPLAY_NUMBER_REPORTS = "HeaderTitleFragment.number_reports";

    private Integer mParamMsgId;
    private boolean mDisplayNumberReports;
    private Integer mParamInstId;

    private ProgressBar mProgressBarHeader; //progressbar_header
    private TextView mTextViewHeaderMsg;
    private ImageView mImageViewInstitutionTypeIcon;
    private TextView mTextViewInstitutionName;
    private TextView mTextViewInstitutionCompleteName;

    private boolean mLoadingHeader;
    private PublicInstitution mPublicInstitution;
    private PublicInstitutionManager mInstitutionManager;


    public static HeaderTitleFragment newInstance(Integer instId, Integer msgId)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, instId);
        args.putInt(EXTRA_MSG_ID, msgId);

        HeaderTitleFragment fragment = new HeaderTitleFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static HeaderTitleFragment newInstance(Integer instId, boolean displayNumberReports)
    {
        Bundle args = new Bundle();
        args.putInt(EXTRA_INST_ID, instId);
        args.putBoolean(EXTRA_DISPLAY_NUMBER_REPORTS, displayNumberReports);

        HeaderTitleFragment fragment = new HeaderTitleFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public HeaderTitleFragment()
    {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null)
        {
            mParamMsgId = args.getInt(EXTRA_MSG_ID, -1);

            mDisplayNumberReports = args.getBoolean(EXTRA_DISPLAY_NUMBER_REPORTS);

            mParamInstId = getArguments().getInt(EXTRA_INST_ID);
            fetchPublicInstitution();
        }
    }

    private void fetchPublicInstitution()
    {
        mInstitutionManager = PublicInstitutionManager.get(getActivity().getApplicationContext());
        mPublicInstitution = mInstitutionManager.getReportedInstitutionById(mParamInstId);

        if (mPublicInstitution == null)
        {
            mLoadingHeader = true;

            mInstitutionManager = PublicInstitutionManager.get(getActivity());
            mInstitutionManager.fetchPublicInstitutionById(mParamInstId, new GetPublicInstitutionCallbacks() {
                @Override
                public void done(PublicInstitution returnedInstitution) {
                    try {
                        if (returnedInstitution != null) {
                            mPublicInstitution = returnedInstitution;

                            mLoadingHeader = false;

                            updateUI();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_header_title, container, false);

        mProgressBarHeader = (ProgressBar) v.findViewById(R.id.progress_bar_header);

        mTextViewHeaderMsg = (TextView) v.findViewById(R.id.textview_header_msg);
        if (mParamMsgId > 0) {
            mTextViewHeaderMsg.setText(mParamMsgId);
        } else {
            mTextViewHeaderMsg.setVisibility(View.GONE);
        }

        mImageViewInstitutionTypeIcon = (ImageView) v.findViewById(R.id.imageview_institution_type_icon);
        mTextViewInstitutionName = (TextView) v.findViewById(R.id.textview_institution_name);

        mTextViewInstitutionCompleteName = (TextView) v.findViewById(R.id.textview_institution_complete_name);
        mTextViewInstitutionCompleteName.setVisibility(View.GONE);

        updateUI();

        return v;
    }

    private void updateUI()
    {
        if (!mLoadingHeader)
        {
            mProgressBarHeader.setVisibility(View.GONE);

            if (mPublicInstitution != null) {
                mImageViewInstitutionTypeIcon.setImageResource(mPublicInstitution.getPlaceBlackIconId());

                String abbrev = mPublicInstitution.getNomeAbreviado();
                String nome = mPublicInstitution.getNome();
                if ((abbrev != null) && (abbrev.length() > 0)) {
                    mTextViewInstitutionName.setText(abbrev);
                } else {
                    mTextViewInstitutionName.setText(nome);
                }

                if (mDisplayNumberReports)
                {
                    int nReports = mInstitutionManager.getNumberReportsForInstitution(mPublicInstitution);
                    String msg = null;

                    if (nReports == 1) {
                        msg = nReports + " " + getResources().getString(R.string.msg_report);
                    } else {
                        msg = nReports + " " + getResources().getString(R.string.msg_reports);
                    }
                    mTextViewHeaderMsg.setText(msg);
                    mTextViewHeaderMsg.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            mProgressBarHeader.setVisibility(View.VISIBLE);
        }
    }
}
