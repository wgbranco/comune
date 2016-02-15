package williamgbranco.comune.activity;

import android.util.Log;

import williamgbranco.comune.R;

public class DisplayReportResponseActivity extends HeaderContentActivity
{
    public static final String TAG = "DplayReportRespActv";
    public static final String EXTRA_INST_ID = "DisplayReportResponseActivity.institution_id";
    public static final String EXTRA_REPORT_ID = "DisplayReportResponseActivity.report_id";
    public static final String EXTRA_RESPONSE_ID = "DisplayReportResponseActivity.response_id";

    @Override
    protected HeaderTitleFragment createHeaderFragment()
    {
        Integer instID = getIntent().getIntExtra(EXTRA_INST_ID, -1);

        return HeaderTitleFragment.newInstance(instID, false);
    }

    @Override
    protected DisplayReportResponseFragment createContentFragment()
    {
        Integer instId = getIntent().getIntExtra(EXTRA_INST_ID, -1);
        Integer reportId = getIntent().getIntExtra(EXTRA_REPORT_ID, -1);
        Integer responseId = getIntent().getIntExtra(EXTRA_RESPONSE_ID, -1);

        Log.i(TAG, "EXTRA_INST_ID: " + instId + ", EXTRA_REPORT_ID: "+reportId + ", EXTRA_RESPONSE_ID: "+responseId);
        return DisplayReportResponseFragment.newInstance(instId, reportId, responseId);
    }

    @Override
    protected String getEmptyDataMessage() {
        return getErrorMessage();
    }

    @Override
    protected String getErrorMessage() {
        return getResources().getString(R.string.msg_error_loading_list);
    }

}
