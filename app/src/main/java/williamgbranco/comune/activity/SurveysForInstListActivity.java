package williamgbranco.comune.activity;


import android.os.Bundle;

import williamgbranco.comune.R;
import williamgbranco.comune.manager.PublicInstitutionManager;


public class SurveysForInstListActivity extends HeaderContentActivity
{
    public static final String EXTRA_INST_ID = "SurveyListActv.institution_id";

    private PublicInstitutionManager mInstitutionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mInstitutionManager = PublicInstitutionManager.get(this);
        /*int instID = getIntent().getIntExtra(EXTRA_INST_ID, -1);
        mInstitutionManager = PublicInstitutionManager.get(this);
        PublicInstitution institution = mInstitutionManager.getPublicInstitutionById(instID);
        if (institution != null) {
            mInstitutionManager.setCurrentInstitution(institution);
        } else {
            this.finish();
        }*/
    }

    @Override
    protected InstitutionHeaderFragment createHeaderFragment()
    {
        Integer instID = getIntent().getIntExtra(EXTRA_INST_ID, -1);

        return InstitutionHeaderFragment.newInstance(instID);
    }

    @Override
    protected SurveysForInstListFragment createContentFragment()
    {
        Integer instID = getIntent().getIntExtra(EXTRA_INST_ID, -1);

        return SurveysForInstListFragment.newInstance(instID);
    }

    @Override
    protected String getEmptyDataMessage() {
        return getErrorMessage();
    }

    @Override
    protected String getErrorMessage() {
        return getResources().getString(R.string.msg_error_loading_list);
    }

    @Override
    protected void onDestroy() {
        mInstitutionManager.setCurrentInstitution(null);
        super.onDestroy();
    }

}
