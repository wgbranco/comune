package williamgbranco.comune.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import williamgbranco.comune.R;


public abstract class HeaderContentFooterActivity extends HeaderContentActivity
{
    private static final String TAG = "HeaderContentFooterActv";

    protected abstract Fragment createFooterFragment();

    @Override
    protected int getContentViewFragment()
    {
        return R.layout.fragment_header_content_footer_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void configFragments()
    {
        Log.i(TAG, "configFragments");
        configHeaderFragment();
        configContentFragment();
        configFooterFragment();
    }

    protected void configFooterFragment()
    {
        FragmentManager fm = getSupportFragmentManager();

        Fragment footerFragment = fm.findFragmentById(R.id.footer_container);

        if (footerFragment == null)
        {
            //Log.i(TAG, "footerFragment is NULL");
            footerFragment = createFooterFragment();
            fm.beginTransaction()
                    .replace(R.id.footer_container, footerFragment)
                    .commit();
        }
        //else {
            //Log.i(TAG, "footerFragment is NOT NULL");
        //}
    }

}
