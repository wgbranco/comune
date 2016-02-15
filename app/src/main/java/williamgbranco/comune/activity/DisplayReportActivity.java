package williamgbranco.comune.activity;

import williamgbranco.comune.R;


public class DisplayReportActivity extends HeaderContentActivity
{
    private static final String TAG = "comune.ReportLstInstAtv";

    public static final String EXTRA_INST_ID = "DisplayReportActivity.institution_id";
    public static final String EXTRA_REPORT_ID = "DisplayReportActivity.report_id";

    @Override
    protected HeaderTitleFragment createHeaderFragment()
    {
        Integer instID = getIntent().getIntExtra(EXTRA_INST_ID, -1);

        return HeaderTitleFragment.newInstance(instID, false);
    }

    @Override
    protected DisplayReportFragment createContentFragment()
    {
        Integer instId = getIntent().getIntExtra(EXTRA_INST_ID, -1);
        Integer reportId = getIntent().getIntExtra(EXTRA_REPORT_ID, -1);

        return DisplayReportFragment.newInstance(instId, reportId);
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
