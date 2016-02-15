package williamgbranco.comune.activity;

import android.util.Log;

import williamgbranco.comune.callback.MapCallbacks;

public class BaseMapActivity extends SingleFragmentActivity implements MapCallbacks
{
    private static final String TAG = "comune.BaseMapActivity";

    public static final String EXTRA_ACTION = "williamgbranco.comune.activity.BaseMapActivity.extra.action";


    private static BaseMapFragment sBaseMapFragment;

    @Override
    protected BaseMapFragment createFragment()
    {
        String action = getIntent().getStringExtra(EXTRA_ACTION);
        Log.i(TAG, "EXTRA_ACTION: "+action);

        sBaseMapFragment = BaseMapFragment.newInstance(action);
        return sBaseMapFragment;
    }

    /*public void onInstitutionMarkerClick(PublicInstitution pInstitution)
    {
        sBaseMapFragment.onInstitutionMarkerClick(pInstitution);
    }*/

    @Override
    public void onSearchComplete()
    {
        sBaseMapFragment.onSearchComplete();
    }
}
