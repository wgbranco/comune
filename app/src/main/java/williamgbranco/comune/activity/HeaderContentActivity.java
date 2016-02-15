package williamgbranco.comune.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import williamgbranco.comune.R;
import williamgbranco.comune.callback.LoadingCompleteCallback;

public abstract class HeaderContentActivity extends FragmentActivity implements LoadingCompleteCallback
{
    private static final String TAG = "HeaderContentActivity";

    public static final String EXTRA_CONFIG_FRAGS = "HeaderContentActivity.config_fragments";

    protected ProgressBar mProgressBar;
    protected TextView mTextViewEmptyList;
    protected TextView mTextViewErrorFetchingList;
    protected LinearLayout mFragmentContainer;


    protected abstract Fragment createHeaderFragment();

    protected abstract Fragment createContentFragment();

    protected abstract String getEmptyDataMessage();

    protected abstract String getErrorMessage();

    protected int getContentViewFragment()
    {
        return R.layout.fragment_header_content_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewFragment());

        mFragmentContainer = (LinearLayout) findViewById(R.id.content_container);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if (mProgressBar != null) {
            mProgressBar.setIndeterminate(true);
            mProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.logo_orange), android.graphics.PorterDuff.Mode.SRC_ATOP);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        mTextViewEmptyList = (TextView) findViewById(R.id.textview_empty_list);
        mTextViewEmptyList.setVisibility(View.INVISIBLE);

        mTextViewErrorFetchingList = (TextView) findViewById(R.id.textview_error_fetching_list);
        mTextViewErrorFetchingList.setVisibility(View.INVISIBLE);

        Bundle args = getIntent().getExtras();
        if (args != null)
        {
            boolean configFragments = args.getBoolean(EXTRA_CONFIG_FRAGS, true);
            Log.i("HeaderContentActivity", "configFragments: " + configFragments);
            if (configFragments)
                configFragments();
        }
        else {
            configFragments();
        }
    }

    protected void configFragments()
    {
        Log.i(TAG, "configFragments");
        configHeaderFragment();
        configContentFragment();
    }

    protected void configHeaderFragment()
    {
        FragmentManager fm = getSupportFragmentManager();

        Fragment headerFragment = fm.findFragmentById(R.id.header_container);

        if (headerFragment == null)
        {
            //Log.i(TAG, "headerFragment is NULL");
            headerFragment = createHeaderFragment();
            fm.beginTransaction()
                    .replace(R.id.header_container, headerFragment)
                    .commit();
        }
        else {
            //Log.i(TAG, "headerFragment is NOT NULL");
        }
    }

    protected void configContentFragment()
    {
        FragmentManager fm = getSupportFragmentManager();

        Fragment contentFragment = fm.findFragmentById(R.id.content_container);

        if (contentFragment == null)
        {
            //Log.i(TAG, "contentFragment is NULL");
            contentFragment = createContentFragment();
            fm.beginTransaction()
                    .replace(R.id.content_container, contentFragment)
                    .commit();
        }
        else {
            //Log.i(TAG, "contentFragment is NOT NULL");
        }
    }

    @Override
    public void startLoadingData()
    {
        mFragmentContainer.setVisibility(View.INVISIBLE);
        mTextViewErrorFetchingList.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void emptyDataSetLoaded()
    {
        mFragmentContainer.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextViewErrorFetchingList.setText(getEmptyDataMessage() + ".");
        mTextViewErrorFetchingList.setTextColor(getResources().getColor(android.R.color.black));
        mTextViewErrorFetchingList.setVisibility(View.VISIBLE);
    }

    @Override
    public void errorLoadingData()
    {
        mFragmentContainer.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextViewErrorFetchingList.setText(getErrorMessage()+".");
        mTextViewErrorFetchingList.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        mTextViewErrorFetchingList.setVisibility(View.VISIBLE);
    }

    @Override
    public void finishLoadingData()
    {
        mTextViewErrorFetchingList.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mFragmentContainer.setVisibility(View.VISIBLE);
    }

}
