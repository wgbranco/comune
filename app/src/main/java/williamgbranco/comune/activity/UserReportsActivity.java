package williamgbranco.comune.activity;


import williamgbranco.comune.R;


public class UserReportsActivity extends HeaderContentActivity
{
    private static final String TAG = "comune.UserReportsActv";


    @Override
    protected HeaderTextFragment createHeaderFragment()
    {
        return HeaderTextFragment.newInstance(R.string.msg_header_institutions_report_list);
    }

    @Override
    protected InstitutionListFragment createContentFragment()
    {
        return new InstitutionListFragment();
    }

    @Override
    protected String getEmptyDataMessage() {
        return getResources().getString(R.string.msg_error_empty_report_list_loaded);
    }

    @Override
    protected String getErrorMessage() {
        return getResources().getString(R.string.msg_error_loading_list);
    }
}
