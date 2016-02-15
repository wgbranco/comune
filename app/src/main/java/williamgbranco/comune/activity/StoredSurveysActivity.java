package williamgbranco.comune.activity;

import android.os.Bundle;
import android.util.Log;

import williamgbranco.comune.R;

public class StoredSurveysActivity extends HeaderContentActivity
{
    private static final String TAG = "StoredSurveysActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    protected StoredSurveysListFragment createContentFragment()
    {
        return new StoredSurveysListFragment();
    }

    @Override
    protected HeaderTextFragment createHeaderFragment()
    {
        return HeaderTextFragment.newInstance(R.string.msg_header_institutions_survey_list);
    }

    @Override
    protected String getEmptyDataMessage() {
        return getResources().getString(R.string.msg_error_empty_survey_list_loaded);
    }

    @Override
    protected String getErrorMessage() {
        return getResources().getString(R.string.msg_error_loading_list);
    }
}
