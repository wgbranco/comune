package williamgbranco.comune.activity;


import williamgbranco.comune.R;

public class ReportListForInstitutionActivity extends HeaderContentActivity //implements LoadingCompleteCallback
{
    private static final String TAG = "comune.ReportLstInstAtv";

    public static final String EXTRA_INST_ID = "ReportListForInstActv.institution_id";


    @Override
    protected HeaderTitleFragment createHeaderFragment()
    {
        Integer instID = getIntent().getIntExtra(EXTRA_INST_ID, -1);

        return HeaderTitleFragment.newInstance(instID, true);
    }

    @Override
    protected ReportListForInstitutionFragment createContentFragment()
    {
        Integer instID = getIntent().getIntExtra(EXTRA_INST_ID, -1);

        return ReportListForInstitutionFragment.newInstance(instID);
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
